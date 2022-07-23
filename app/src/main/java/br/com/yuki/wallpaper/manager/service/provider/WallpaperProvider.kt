package br.com.yuki.wallpaper.manager.service.provider

import android.content.Context
import android.view.SurfaceHolder
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.model.Configuration

interface WallpaperProvider {

    fun interface Callback {
        fun change(image: Image)
    }

    fun play()

    fun pause()

    fun next()

    fun apply(configuration: Configuration)

    fun onDestroy()

    fun onCreate(context: Context, configuration: Configuration)

    fun addImageChangeListener(callback: WallpaperProvider.Callback): Boolean

    fun removeImageChangeListener(callback: WallpaperProvider.Callback): Boolean

    fun surfaceCreated(holder: SurfaceHolder)

    fun surfaceChanged(holder: SurfaceHolder)

    fun surfaceDestroyed()

}