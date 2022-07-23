package br.com.yuki.wallpaper.manager.database.model.image.relation

import android.os.Parcelable
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.image.Image
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageAlbum(
    val album: Album,
    val firstImage: Image?
) : Parcelable
