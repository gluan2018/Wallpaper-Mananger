package br.com.yuki.wallpaper.manager.util.base.model

import android.content.Context
import br.com.yuki.wallpaper.manager.AppComponent
import br.com.yuki.wallpaper.manager.service.provider.BitmapProvider
import br.com.yuki.wallpaper.manager.service.provider.WallpaperProvider

interface AppFactory {

    fun wallpaperProvider(context: Context): WallpaperProvider

    fun bitmapProvider(): BitmapProvider

    companion object {

        fun create(): AppFactory = AppComponent()

    }

}
