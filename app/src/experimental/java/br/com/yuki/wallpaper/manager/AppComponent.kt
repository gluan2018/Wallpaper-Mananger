package br.com.yuki.wallpaper.manager

import android.content.Context
import br.com.yuki.wallpaper.manager.database.manager.AppDatabase
import br.com.yuki.wallpaper.manager.implementations.BitmapProviderImpl
import br.com.yuki.wallpaper.manager.implementations.DataProviderImpl
import br.com.yuki.wallpaper.manager.implementations.WallpaperProviderImpl
import br.com.yuki.wallpaper.manager.service.provider.BitmapProvider
import br.com.yuki.wallpaper.manager.service.provider.WallpaperProvider
import br.com.yuki.wallpaper.manager.util.base.model.AppFactory

class AppComponent : AppFactory {

    override fun bitmapProvider(): BitmapProvider = BitmapProviderImpl()

    override fun wallpaperProvider(context: Context): WallpaperProvider = WallpaperProviderImpl(
        database = DataProviderImpl(
            bitmapProvider = bitmapProvider(),
            wallpaperService = AppDatabase.getInstance(context).wallpaper()
        )
    )

}