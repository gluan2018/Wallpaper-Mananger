package br.com.yuki.wallpaper.manager.screen.home.fragment.home

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.database.manager.AppDatabase
import br.com.yuki.wallpaper.manager.database.model.image.EmptyImage
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.databinding.FragmentHomeBinding
import br.com.yuki.wallpaper.manager.model.Configuration
import br.com.yuki.wallpaper.manager.screen.other.HelpActivity
import br.com.yuki.wallpaper.manager.service.ServiceWallpaper
import br.com.yuki.wallpaper.manager.util.global.utility.statusBarHeight
import br.com.yuki.wallpaper.manager.util.tools.Loader
import br.com.yuki.wallpaper.manager.util.tools.Preferences
import br.com.yuki.wallpaper.manager.util.tools.getOrDefault
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    private val viewModel: HomeViewModel by viewModels()

    private val requestAuthorization = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted && !isDefined())
            loadImage(null)
    }

    private val preferencesImage: Preferences<Image> by lazy {
        Preferences.image(requireContext())
    }

    private val preferences: Preferences<Configuration> by lazy {
        Preferences.config(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        preferencesImage.removeObserver()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.titleCurrentAlbum?.updatePadding(top = statusBarHeight)
        binding?.enableAppCurrentAlbum?.isGone = isDefined()

        viewModel.listen().observe(viewLifecycleOwner) { image ->
            binding?.imageCurrentAlbum?.also { imageView ->
                Glide.with(this)
                    .load(image.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
                    .clearOnDetach()
            }
        }

        binding?.enableAppCurrentAlbum?.setOnClickListener {
            context?.apply {
                startActivity(
                    Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                        .putExtra(
                            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(
                                this.packageName,
                                ServiceWallpaper::class.java.canonicalName!!
                            )
                        )
                )
            }
        }
        binding?.helpCurrentAlbum?.setOnClickListener {
            startActivity(
                Intent(requireContext(), HelpActivity::class.java)
            )
        }

        preferencesImage.startObserver()
        preferencesImage.setCallback { image ->
            loadCurrentAlbum(image.takeUnless { it is EmptyImage })
        }

        loadCurrentAlbum(preferencesImage.current?.takeUnless { it is EmptyImage })
    }

    override fun onStart() {
        super.onStart()
        binding?.enableAppCurrentAlbum?.isGone = isDefined()
    }

    private fun loadCurrentAlbum(image: Image? = null) {
        val strongContext = context ?: return
        if (ActivityCompat.checkSelfPermission(strongContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestAuthorization.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

        val wallpaper = AppDatabase.getInstance(requireContext()).wallpaper()
        lifecycleScope.launch(Dispatchers.IO) {
            val albumId = preferences.getOrDefault().albumId
            val album = albumId?.run { wallpaper.findAlbum(this) }

            loadImage(image?.takeIf { isDefined() })

            withContext(Dispatchers.Main) {
                if (!isDefined()) {
                    binding?.titleCurrentAlbum?.text = getString(R.string.user_default_album)
                    binding?.subtitleCurrentAlbum?.text = ""
                } else {
                    binding?.titleCurrentAlbum?.text = getString(R.string.current_album)
                    binding?.subtitleCurrentAlbum?.text = album?.name ?: getString(R.string.default_album)
                }
            }
        }
    }

    private fun isDefined(): Boolean {
        val strongContext = context ?: return false
        return WallpaperManager.getInstance(strongContext)
            .wallpaperInfo?.packageName?.contains(strongContext.packageName) == true
    }

    private fun loadImage(image: Image?) {
        val strongContext = context ?: return

        val manager = WallpaperManager.getInstance(strongContext)
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                val drawable = if (ActivityCompat.checkSelfPermission(strongContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) null
                else manager.drawable

                withContext(Dispatchers.Main) {
                    binding?.imageCurrentAlbum?.also { background ->
                        background.foreground = ColorDrawable(if (drawable == null) Color.TRANSPARENT else Color.parseColor("#AA000000"))
                        Glide.with(background)
                            .load(image?.path ?: drawable)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(background)
                    }
                }
            }
        }
    }

}