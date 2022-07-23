package br.com.yuki.wallpaper.manager.service

import android.service.wallpaper.WallpaperService
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.model.Configuration
import br.com.yuki.wallpaper.manager.service.provider.WallpaperProvider
import br.com.yuki.wallpaper.manager.util.base.model.AppFactory
import br.com.yuki.wallpaper.manager.util.tools.Preferences

class ServiceWallpaper : WallpaperService() {

    override fun onCreateEngine(): Engine = WallpaperEngine(AppFactory.create().wallpaperProvider(applicationContext))

    private inner class WallpaperEngine(
        private val provider: WallpaperProvider
    ) : Engine() {

        private val gestureDetector: GestureDetector by lazy {
            GestureDetector(applicationContext, callbackDetector)
        }

        private val callbackDetector = object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                provider.next()
                return super.onDoubleTap(e)
            }
        }

        private val callbackImage = WallpaperProvider.Callback { newImage ->
            preferencesImage.current = newImage
        }

        private val preferences: Preferences<Configuration> = Preferences.config(applicationContext)
        private val preferencesImage: Preferences<Image> = Preferences.image(applicationContext)

        private var configuration: Configuration = preferences.current ?: Configuration.DEFAULT

        init {
            setTouchEventsEnabled(true)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible)
                provider.play()
            else
                provider.pause()
        }

        override fun onTouchEvent(event: MotionEvent?) {
            gestureDetector.onTouchEvent(event)
            super.onTouchEvent(event)
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            provider.onCreate(displayContext!!, configuration)
            preferences.startObserver()
            preferences.setCallback { newConfiguration ->
                this.configuration = newConfiguration
                provider.apply(newConfiguration)
            }

            provider.addImageChangeListener(callbackImage)
            provider.play()
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            if (holder != null)
                provider.surfaceCreated(holder)
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder?) {
            if (holder != null)
                provider.surfaceChanged(holder)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            if (holder != null)
                provider.surfaceChanged(holder)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) = provider.surfaceDestroyed()

        override fun onDestroy() = provider.onDestroy()
    }
}