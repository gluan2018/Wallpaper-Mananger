package br.com.yuki.wallpaper.manager.gl

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.view.Surface
import br.com.yuki.wallpaper.manager.R
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRender(
    private val context: Context
) : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private var updateSurface: Boolean = false

    private val textureRender: TextureRender = TextureRender()
    private var surfaceTexture: SurfaceTexture? = null

    private val mediaPlayer = MediaPlayer.create(context, R.raw.arknights)

    init {

        CoroutineScope(SupervisorJob()).launch(Dispatchers.Main) {
            delay(2000)
            mediaPlayer.start()
        }
    }

    @Synchronized
    override fun onFrameAvailable(p0: SurfaceTexture?) {
        updateSurface = true
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        textureRender.surfaceCreated()

        surfaceTexture = SurfaceTexture(textureRender.textureId)
        surfaceTexture?.setOnFrameAvailableListener(this)

        val surface = Surface(surfaceTexture)
        mediaPlayer.setSurface(surface)
        surface.release()

        mediaPlayer.setOnCompletionListener {
            it.setSurface(null)
            ExoPlayer.Builder(context)
                .build()

            Surface(surfaceTexture).apply {
                val lockHardwareCanvas = lockHardwareCanvas()
                lockHardwareCanvas.drawColor(Color.RED)
                unlockCanvasAndPost(lockHardwareCanvas)
                release()
            }
        }

        synchronized(this) {
            updateSurface = false;
        }
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {

    }

    override fun onDrawFrame(p0: GL10?) {
        synchronized(this) {
            if (updateSurface) {
                surfaceTexture?.updateTexImage()
                updateSurface = false
            }
        }

        surfaceTexture?.let { surface ->
            textureRender.drawFrame(surface)
        }
    }

}