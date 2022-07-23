package br.com.yuki.wallpaper.manager.service.provider.helper

import android.graphics.Bitmap
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.image.Image

data class SurfaceImage(
    val album: Album,
    val image: Image,
    val bitmap: Bitmap
)
