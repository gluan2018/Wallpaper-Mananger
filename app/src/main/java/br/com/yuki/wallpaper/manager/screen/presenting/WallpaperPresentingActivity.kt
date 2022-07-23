package br.com.yuki.wallpaper.manager.screen.presenting

import android.content.res.Configuration
import android.os.Bundle
import android.util.Size
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.database.model.image.relation.ImageAlbum
import br.com.yuki.wallpaper.manager.databinding.ActivityWallpaperPresentingBinding
import br.com.yuki.wallpaper.manager.screen.presenting.adapter.ImagesAdapter
import br.com.yuki.wallpaper.manager.screen.presenting.dialog.LoadingImagesDialog
import br.com.yuki.wallpaper.manager.util.base.factory.wallpaperViewModels
import br.com.yuki.wallpaper.manager.util.global.utility.GetContent
import br.com.yuki.wallpaper.manager.util.tools.Loader
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WallpaperPresentingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ALBUM = "extra_album"
        private const val CURRENT_INDEX = "index_selected"
        private const val CURRENT_IMAGE = "image_selected"
        private const val CURRENT_LIST_IMAGE = "images_saved"
    }

    private lateinit var album: ImageAlbum
    private lateinit var binding: ActivityWallpaperPresentingBinding

    private val viewModel: WallpaperPresentingViewModel by wallpaperViewModels()

    private val requestImage: ActivityResultLauncher<Pair<Album, Array<String>>> = registerForActivityResult(GetContent()) { callback ->
        if (callback == null)
            return@registerForActivityResult

        val loading = LoadingImagesDialog()
        loading.show(supportFragmentManager, null)

        lifecycleScope.launch(Dispatchers.Main) {
            runCatching {
                val callbackData = withContext(Dispatchers.Main) { callback.result() }
                val imageSize = binding.CurrentImage.run { Size(width, height) }

                viewModel.save(applicationContext, imageSize, callbackData).observe(this@WallpaperPresentingActivity) { image ->
                    loading.setProgress(image.current, image.max, image.image)

                    if (image.current == image.max)
                        loading.dismiss()

                    if (image.image == null)
                        return@observe

                    if (adapter.itemCount == 0) {
                        loadingImage(image.image)
                        adapter.currentIndex = 0
                        adapter.currentSelected = image.image
                    }
                    adapter.add(image.image)
                }
            }.onFailure {
                it.printStackTrace()
                Snackbar.make(binding.root, R.string.error_generic, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private val adapter: ImagesAdapter by lazy {
        ImagesAdapter(albumId = album.album.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWallpaperPresentingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        album = intent.getParcelableExtra(EXTRA_ALBUM) ?: run {
            finishActivity(RESULT_CANCELED)
            return
        }

        bindingView()
        loadingData(bundle = savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(CURRENT_LIST_IMAGE, Bundle().apply {
            putParcelableArray(WallpaperPresentingViewModel.IMAGES, adapter.all().toTypedArray())
        })
        outState.putInt(CURRENT_INDEX, adapter.currentIndex)
        outState.putParcelable(CURRENT_IMAGE, adapter.currentSelected)
    }

    private fun bindingView() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { _, inset ->
            val statusBar = inset.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBar = inset.getInsets(WindowInsetsCompat.Type.navigationBars())

            binding.containerWallpaper.setPadding(navigationBar.left, statusBar.top, navigationBar.right, navigationBar.bottom)

            return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
        }

        binding.root.doOnLayout {
            binding.CardImage.updateLayoutParams<ConstraintLayout.LayoutParams> {
                dimensionRatio = "${binding.root.width}:${binding.root.height}"
            }
        }

        binding.addImage.setOnClickListener {
            requestImage.launch(album.album to arrayOf("image/*"))
        }

        binding.saveImage.setOnClickListener {
            val item = adapter.currentSelected ?: return@setOnClickListener
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                item.vertical = Image.Info(zoom = binding.ScalableImage.zoom, point = binding.ScalableImage.point)
            else
                item.horizontal = Image.Info(zoom = binding.ScalableImage.zoom, point = binding.ScalableImage.point)

            viewModel.update(item).observe(this@WallpaperPresentingActivity) {
                adapter.edit(item)
            }
        }

        binding.deleteImage.setOnClickListener {
            val selected = adapter.currentSelected ?: return@setOnClickListener
            viewModel.delete(image = selected).observe(this) {
                adapter.remove(selected)
                adapter.selectLast()?.run(this::loadingImage) ?: run {
                    binding.CurrentImage.setImageBitmap(null)
                    binding.ScalableImage.setImageBitmap(null)
                }
            }
        }

        binding.ListImages.adapter = adapter
        adapter.attach(binding.ListImages, resources.configuration.orientation)
        adapter.setOnClickListener(this::loadingImage)

        adapter.setOnMoveListener { old, current ->
            viewModel.update(old).observe(this) {}
            viewModel.update(current).observe(this) {}
        }
    }

    private fun loadingData(bundle: Bundle?) {
        viewModel.setState(bundle?.getBundle(CURRENT_LIST_IMAGE) ?: Bundle())
        viewModel.load(album.album.id).observe(this) { items ->
            when {
                bundle?.containsKey(CURRENT_IMAGE) == true && bundle.containsKey(CURRENT_INDEX) -> {
                    adapter.currentSelected = bundle.getParcelable(CURRENT_IMAGE)
                    adapter.currentIndex = bundle.getInt(CURRENT_INDEX, -1)
                    adapter.currentSelected?.apply(this::loadingImage)
                }
                adapter.itemCount == 0 -> items.firstOrNull()?.apply(this::loadingImage)
            }
            adapter.set(items)

            if (adapter.currentIndex >= 0)
                binding.ListImages.scrollToPosition(adapter.currentIndex)
        }
    }

    private fun loadingImage(image: Image) {
        Loader.create(applicationContext)
            .load(image.path)
            .into(binding.ScalableImage)

        Loader.create(applicationContext)
            .load(image.path)
            .into(binding.CurrentImage)

        resources?.apply {
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                binding.ScalableImage.info = image.vertical
            else
                binding.ScalableImage.info = image.horizontal
        }

    }

}