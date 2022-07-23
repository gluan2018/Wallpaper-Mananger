package br.com.yuki.wallpaper.manager.screen.presenting

import android.content.Context
import android.os.Bundle
import android.util.Size
import androidx.lifecycle.liveData
import br.com.yuki.wallpaper.manager.database.model.album.relation.ResponseInsert
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.database.service.WallpaperService
import br.com.yuki.wallpaper.manager.util.base.viewmodel.ViewModelLiveData
import br.com.yuki.wallpaper.manager.util.global.utility.cast
import br.com.yuki.wallpaper.manager.util.tools.Loader
import kotlinx.coroutines.Dispatchers
import java.io.File

class WallpaperPresentingViewModel constructor(
    private val wallpaper: WallpaperService
) : ViewModelLiveData() {

    data class Insert(
        val current: Int,
        val max: Int,
        val image: Image?
    )

    companion object {
        const val IMAGES = "images"
    }

    private var state: Bundle = Bundle()

    fun setState(bundle: Bundle) {
        this.state = bundle
    }

    fun load(albumId: Long) = liveData(Dispatchers.IO) {
        runCatching {
            if (state.containsKey(IMAGES))
                return@runCatching state.getParcelableArray(IMAGES)?.map { it.cast<Image>() }?.toList() ?: emptyList()
            wallpaper.all(albumId)
        }.onSuccess { items ->
            emit(items)
        }.callOnError()
    }

    fun save(context: Context, imageSize: Size, response: ResponseInsert) = liveData(Dispatchers.IO) {
        val loader = Loader.with(context)
        val size = response.imagesUri.size

        val albumFile = File(response.album.paste)
        val currentSize = getSize(albumId = response.album.id)

        for ((index, uriImage) in response.imagesUri.withIndex()) {
            runCatching {
                val imageFile = loader.copy(uriImage, imageSize, albumFile)
                val image = Image(
                    albumId = response.album.id,
                    path = imageFile.path,
                    index = currentSize + index + 1
                )
                image.apply { id = wallpaper.add(image) }
            }.onFailure {
                it.printStackTrace()
            }.onSuccess { item ->
                emit(Insert(index + 1, size, item))
            }
        }
    }

    fun update(image: Image) = liveData(Dispatchers.IO) {
        runCatching {
            wallpaper.update(image)
            emit(Unit)
        }.callOnError()
    }

    fun delete(image: Image) = liveData(Dispatchers.IO) {
        runCatching {
            image.path?.let { path ->
                runCatching {
                    File(path).delete()
                }
            }
            wallpaper.delete(image)
            emit(Unit)
        }.callOnError()
    }

    suspend fun getSize(albumId: Long) = suspend { wallpaper.countImage(albumId) }.invoke()

}
