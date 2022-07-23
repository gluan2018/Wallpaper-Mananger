package br.com.yuki.wallpaper.manager.database.model.album.relation

import android.net.Uri
import br.com.yuki.wallpaper.manager.database.model.album.Album

data class ResponseInsert(
    val album: Album,
    val imagesUri: List<Uri>
)
