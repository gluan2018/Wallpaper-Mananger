package br.com.yuki.wallpaper.manager.service.provider

import androidx.annotation.MainThread
import br.com.yuki.wallpaper.manager.model.Configuration
import br.com.yuki.wallpaper.manager.service.provider.helper.SurfaceImage

interface DatabaseProvider {

    fun interface Callback {
        @MainThread
        fun resource(surfaceImage: SurfaceImage)
    }

    val configuration: Configuration?

    fun reload(configuration: Configuration)

    fun cancel()

    fun load(width: Int, height: Int, isPortrait: Boolean, isReload: Boolean = false)

    fun loadLockScreen(width: Int, height: Int, isPortrait: Boolean, isReload: Boolean = false)

    fun addOnResourceLoadListener(listener: Callback)

    fun removeOnResourceLoadListener(listener: Callback)

}