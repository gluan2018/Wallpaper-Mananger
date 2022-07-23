package br.com.yuki.wallpaper.manager.service.provider

import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.InputStream

interface BitmapProvider {

    @Throws(IllegalArgumentException::class, OutOfMemoryError::class, Throwable::class)
    fun bitmap(uri: Uri, requiredWidth: Int, requiredHeight: Int): Bitmap

    @Throws(IllegalArgumentException::class, OutOfMemoryError::class, Throwable::class)
    fun bitmap(input: File, requiredWidth: Int, requiredHeight: Int): Bitmap

}