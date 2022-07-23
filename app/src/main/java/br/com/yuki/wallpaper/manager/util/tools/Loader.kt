package br.com.yuki.wallpaper.manager.util.tools

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.util.Size
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.doOnLayout
import br.com.yuki.wallpaper.manager.service.provider.BitmapProvider
import br.com.yuki.wallpaper.manager.util.base.model.AppFactory
import br.com.yuki.wallpaper.manager.util.global.utility.cast
import br.com.yuki.wallpaper.manager.util.global.utility.copy
import kotlinx.coroutines.*
import java.io.File

interface Load {
    suspend fun copy(uri: Uri, size: Size, target: File): File
}

abstract class Loader protected constructor(
    protected val bitmapProvider: BitmapProvider
) {

    companion object {

        fun create(context: Context): Loader = ImageLoader(context, AppFactory.create().bitmapProvider())

        fun with(context: Context): Load = CopyLoader(context, AppFactory.create().bitmapProvider())

    }

    abstract fun load(path: String?): Loader

    abstract fun into(view: ImageView)

}

private class ImageLoader(
    private val context: Context,
    bitmapProvider: BitmapProvider
) : Loader(bitmapProvider) {

    private val coroutineScope = CoroutineScope(SupervisorJob())
    private var path: String? = null

    override fun load(path: String?): Loader {
        this.path = path
        return this
    }

    override fun into(view: ImageView) {
        view.doOnLayout { imageView ->
            coroutineScope.launch(Dispatchers.IO) {
                val file = path?.run { toUri() } ?: return@launch
                val bitmap = bitmapProvider.bitmap(file, view.width, view.height)
                withContext(Dispatchers.Main) {
                    val drawable = TransitionDrawable(arrayOf(ColorDrawable(Color.TRANSPARENT), BitmapDrawable(context.resources, bitmap)))
                    drawable.isCrossFadeEnabled = true
                    drawable.startTransition(300)

                    imageView.cast<ImageView>().setImageDrawable(drawable)
                }
            }
        }
    }
}

private class CopyLoader(
    private val context: Context,
    private val bitmapProvider: BitmapProvider
) : Load {

    override suspend fun copy(uri: Uri, size: Size, target: File): File = withContext(Dispatchers.IO) {
        val inputStream = when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> uri.copy(context, target)
            else -> throw IllegalArgumentException("scheme is not ${ContentResolver.SCHEME_CONTENT}")
        }

        val currentImage = bitmapProvider.bitmap(inputStream, size.width, size.height).let { baseState ->
            TransformationUtil.scaleToLowIfNecessary(baseState, size)
        }

        currentImage.compress(Bitmap.CompressFormat.PNG, 100, inputStream.outputStream())
        currentImage.recycle()

        return@withContext inputStream
    }
}