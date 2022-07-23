package br.com.yuki.wallpaper.manager.gl

import android.view.SurfaceHolder

class OpenGLWallpaperService : GLWallpaperService() {

    override fun onCreateEngine(): Engine = OpenGLWallpaperEngine()

    inner class OpenGLWallpaperEngine : GLWallpaperService.GLEngine() {
        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            setEGLContextClientVersion(2)
            setRenderer(OpenGLRender(applicationContext))
        }
    }

}