package br.com.yuki.wallpaper.manager.screen.home.fragment.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.databinding.FragmentAlarmAlbumBinding
import br.com.yuki.wallpaper.manager.screen.home.fragment.alarm.dialog.adapter.AdapterAlarmAlbum
import br.com.yuki.wallpaper.manager.util.global.OnClickListener
import br.com.yuki.wallpaper.manager.util.base.factory.wallpaperViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlarmAlbumBottomFragment : BottomSheetDialogFragment() {

    private var binding: FragmentAlarmAlbumBinding? = null
    private var onSelectListener: OnClickListener<Album>? = null

    private val viewModel: AlarmAlbumViewModel by wallpaperViewModels()
    private val adapter: AdapterAlarmAlbum = AdapterAlarmAlbum().apply {
        setOnClickListener { album ->
            onSelectListener?.onClick(album.album)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlarmAlbumBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.albumsAlarmAlbum?.adapter = adapter
        viewModel.load().observe(viewLifecycleOwner, adapter::set)
    }

    fun setOnSelectListener(callback: OnClickListener<Album>?): AlarmAlbumBottomFragment {
        onSelectListener = callback
        return this
    }

}