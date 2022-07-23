package br.com.yuki.wallpaper.manager.screen.home.fragment.album

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.image.relation.ImageAlbum
import br.com.yuki.wallpaper.manager.databinding.FragmentAlbumBinding
import br.com.yuki.wallpaper.manager.model.Configuration
import br.com.yuki.wallpaper.manager.screen.home.fragment.album.adapter.AlbumsAdapter
import br.com.yuki.wallpaper.manager.screen.presenting.WallpaperPresentingActivity
import br.com.yuki.wallpaper.manager.util.base.factory.wallpaperViewModels
import br.com.yuki.wallpaper.manager.util.global.utility.statusBarHeight
import br.com.yuki.wallpaper.manager.util.tools.*
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.*

class AlbumFragment : Fragment() {

    private val viewModel: AlbumsViewModel by wallpaperViewModels()

    private var binding: FragmentAlbumBinding? = null
    private var lastOpened: ImageAlbum? = null

    private val preferences: Preferences<Configuration> by lazy {
        Preferences.config(requireContext())
    }

    private val albumsAdapter: AlbumsAdapter = AlbumsAdapter().apply {
        setOnApplyListener { album ->
            preferences.current = preferences.getOrDefault().copy(albumId = album.album.id)
            binding?.root?.apply {
                Snackbar.make(this, R.string.apply_changes, Snackbar.LENGTH_SHORT).show()
            }
        }
        setOnDeleteListener { album ->
            viewModel.delete(album).observe(viewLifecycleOwner) {
                this.remove(album)
                if (this.itemCount == 0)
                    binding?.EmptyAlbum?.visibility = View.VISIBLE
            }
        }
        setOnEditListener { album ->
            viewModel.update(album).observe(viewLifecycleOwner) { image ->
                this.edit(image)
            }
        }
    }

    private val requestChangeAlbum: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result == null)
            return@registerForActivityResult

        lastOpened?.apply {
            viewModel.reloadAlbum(this).observe(this@AlbumFragment.viewLifecycleOwner) { album ->
                albumsAdapter.edit(album)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlbumBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.root?.updatePadding(top = statusBarHeight)

        bindingView()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun bindingView() {
        binding?.ImageList?.adapter = albumsAdapter
        binding?.AddAlbumImage?.setOnClickListener {
            viewModel.add(
                Album(name = "Album #${albumsAdapter.itemCount}", paste = albumsPaste().path)
            ).observe(viewLifecycleOwner) { newAlbum ->
                if (newAlbum != null) {
                    if (albumsAdapter.itemCount == 0)
                        binding?.EmptyAlbum?.visibility = View.GONE
                    albumsAdapter.add(newAlbum)
                }
            }
        }

        albumsAdapter.setOnClickListener { album ->
            lastOpened = album
            requestChangeAlbum.launch(
                Intent(requireContext(), WallpaperPresentingActivity::class.java)
                    .putExtra(WallpaperPresentingActivity.EXTRA_ALBUM, album)
            )
        }
        viewModel.load().observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                binding?.EmptyAlbum?.visibility = View.VISIBLE
            }
            albumsAdapter.set(items)
        }
    }

    private fun albumsPaste(): File = File(
        requireContext().dataDir,
        UUID.randomUUID().toString()
    ).apply(File::mkdir)

}