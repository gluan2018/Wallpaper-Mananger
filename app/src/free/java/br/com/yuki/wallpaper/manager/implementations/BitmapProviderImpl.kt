package br.com.yuki.wallpaper.manager.implementations

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toFile
import br.com.yuki.wallpaper.manager.service.provider.BitmapProvider
import java.io.File

class BitmapProviderImpl : BitmapProvider {

    @Throws(IllegalArgumentException::class, OutOfMemoryError::class, Throwable::class)
    override fun bitmap(uri: Uri, requiredWidth: Int, requiredHeight: Int): Bitmap {
        val inputStream = when (uri.scheme) {
            ContentResolver.SCHEME_FILE -> uri.toFile()
            else -> File(uri.toString())
        }

        return bitmap(inputStream, requiredWidth, requiredHeight)
    }

    @Throws(IllegalArgumentException::class, OutOfMemoryError::class, Throwable::class)
    override fun bitmap(input: File, requiredWidth: Int, requiredHeight: Int): Bitmap = BitmapFactory.Options().run {
        inJustDecodeBounds = true

        BitmapFactory.decodeFile(input.path, this)
        inSampleSize = calculateSize(this, requiredWidth, requiredHeight)

        inJustDecodeBounds = false

        return@run BitmapFactory.decodeFile(input.path, this) ?: throw Throwable("Error load drawable")
    }

    private fun calculateSize(info: BitmapFactory.Options, requiredWidth: Int, requiredHeight: Int): Int {
        val (height: Int, width: Int) = info.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > requiredHeight || width > requiredWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= requiredHeight && halfWidth / inSampleSize >= requiredWidth)
                inSampleSize *= 2
        }

        return inSampleSize
    }

}