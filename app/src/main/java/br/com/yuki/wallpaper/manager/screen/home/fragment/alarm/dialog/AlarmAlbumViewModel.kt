package br.com.yuki.wallpaper.manager.screen.home.fragment.alarm.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.yuki.wallpaper.manager.database.model.image.relation.ImageAlbum
import br.com.yuki.wallpaper.manager.database.service.WallpaperService
import kotlinx.coroutines.launch

class AlarmAlbumViewModel constructor(
    private val wallpaper: WallpaperService
) : ViewModel() {

    fun load(): LiveData<List<ImageAlbum>> {
        val liveData = MutableLiveData<List<ImageAlbum>>()

        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                runCatching {
//                    wallpaper.albumsWithFirstImage()
//                }.onSuccess(liveData::postValue)
//            }
        }

        return liveData
    }

}