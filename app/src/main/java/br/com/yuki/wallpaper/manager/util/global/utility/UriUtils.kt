package br.com.yuki.wallpaper.manager.util.global.utility

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.core.net.toFile
import java.io.File
import java.util.*

@WorkerThread
fun Uri.copy(context: Context, dir: File = context.dataDir, to: File? = null): File {
    return when {
        scheme == ContentResolver.SCHEME_CONTENT -> {
            val mimeType = context.contentResolver.getType(this) ?: throw IllegalArgumentException("Type no found")
            val type = mimeType.substringAfter("/")
            val file = to ?: File(dir, "${UUID.randomUUID()}.$type")

            val inputStream = context.contentResolver.openInputStream(this) ?: throw Throwable()
            inputStream.copyTo(file.outputStream())

            return file
        }
        scheme == ContentResolver.SCHEME_FILE -> toFile()
        path != null -> File(path!!)
        else -> throw IllegalArgumentException()
    }
}