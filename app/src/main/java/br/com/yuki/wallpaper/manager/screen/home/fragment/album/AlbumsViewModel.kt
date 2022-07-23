package br.com.yuki.wallpaper.manager.screen.home.fragment.album

import androidx.lifecycle.liveData
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.image.relation.ImageAlbum
import br.com.yuki.wallpaper.manager.database.service.WallpaperService
import br.com.yuki.wallpaper.manager.util.base.viewmodel.ViewModelLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.io.File

class AlbumsViewModel(
    private val wallpaper: WallpaperService
) : ViewModelLiveData() {

    //region Crud Album

    fun load() = liveData(Dispatchers.IO) {
        delay(300)
        runCatching {
            wallpaper.imageAlbum()
        }.emit(this)
    }

    /**
     * Insert album in database, when is complete, is posted to [postAlbums]
     * @param album Who will be insert
     */

    fun add(album: Album) = liveData(Dispatchers.IO) {
        runCatching {
            val id = wallpaper.add(album)
            wallpaper.findAlbumWithImage(id)
        }.emit(this)
    }

    /**
     * Deletes album in database, when is complete, is posted to [postAlbums]
     * @param album Who will be deleted
     */
    fun delete(album: ImageAlbum) = liveData(Dispatchers.IO) {
        runCatching {
            val file = File(album.album.paste)

            if (file.exists()) {
                val isDeleted = file.deleteRecursively()
                if (!isDeleted)
                    throw Throwable("Error delete album")
            }
            wallpaper.delete(album.album)
        }.emit(this)
    }

    /**
     * Updates album data in database, when is complete, is posted to [postAlbums]
     * @param album Who will be updated
     */
    fun update(album: ImageAlbum) = liveData(Dispatchers.IO) {
        runCatching {
            wallpaper.update(album.album)
            emit(album)
        }.callOnError()
    }

    fun reloadAlbum(album: ImageAlbum) = liveData(Dispatchers.IO) {
        runCatching {
            wallpaper.findAlbumWithImage(album.album.id)
        }.emit(this)
    }

    //endregion

}