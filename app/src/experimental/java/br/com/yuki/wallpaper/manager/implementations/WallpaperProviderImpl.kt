package br.com.yuki.wallpaper.manager.implementations

import android.animation.ObjectAnimator
import android.app.KeyguardManager
import android.content.Context
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import androidx.core.animation.addListener
import br.com.yuki.wallpaper.manager.model.Configuration
import br.com.yuki.wallpaper.manager.service.provider.DatabaseProvider
import br.com.yuki.wallpaper.manager.service.provider.WallpaperProvider
import br.com.yuki.wallpaper.manager.service.provider.helper.SurfaceImage
import br.com.yuki.wallpaper.manager.util.global.utility.cast
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class WallpaperProviderImpl(
    private val database: DatabaseProvider
) : WallpaperProvider {

    private var currentImage: SurfaceImage? = null
    private var lastImage: SurfaceImage? = null

    private val coroutineScope = CoroutineScope(SupervisorJob())
    private var job: Job? = null

    private var weakContext: WeakReference<Context>? = null

    private val callback = DatabaseProvider.Callback { surfaceImage ->
        synchronizer {
            lastImage = currentImage
            currentImage = surfaceImage
            draw()
        }
    }

    private var locker = Any()
    private var listener: MutableList<WallpaperProvider.Callback> = mutableListOf()

    private var isVisible: Boolean = false
    private var holder: SurfaceHolder? = null

    private var animationIsRunning: Boolean = false

    private val lastPaint = Paint()
    private val paint = Paint()

    private val objectAnimator = ObjectAnimator.ofInt(0, 255).apply {
        addUpdateListener {
            paint.alpha = it.animatedValue as Int
            lastPaint.alpha = 255 - paint.alpha
            onDraw()
        }
        addListener(onEnd = {
            lastImage = null
            animationIsRunning = false

            val image = currentImage?.image ?: return@addListener
            listener.forEach { call ->
                call.change(image)
            }
        })
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    private val isLockScreen: Boolean
        get() {
            val keyguardManager = weakContext?.get()?.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager ?: return false
            return keyguardManager.isDeviceLocked
        }

    private val isPortrait: Boolean
        get() = weakContext?.get()?.run {
            val orientation = getSystemService(Context.WINDOW_SERVICE).cast<WindowManager>().run {
                display?.rotation ?: Surface.ROTATION_0
            }

            return@run orientation == Surface.ROTATION_0 || orientation == Surface.ROTATION_90
        } ?: true

    override fun play() = synchronizer {
        isVisible = true
        if (isLockScreen) {
            val (height: Int, width: Int) = holder?.surfaceFrame?.run { height() to width() } ?: return@synchronizer
            database.loadLockScreen(width, height, isPortrait)
        } else
            draw()
    }

    override fun pause() = synchronizer { isVisible = false }

    override fun next() = synchronizer {
        val (height: Int, width: Int) = holder?.surfaceFrame?.run { height() to width() } ?: return@synchronizer
        database.load(width, height, isPortrait)
    }

    override fun apply(configuration: Configuration) = synchronizer {
        database.reload(configuration)
        job?.cancel()
        generateJob(configuration)
    }

    override fun onDestroy() = synchronizer {
        database.cancel()
        database.removeOnResourceLoadListener(callback)

        holder = null
        isVisible = false
        listener.clear()
    }

    override fun onCreate(context: Context, configuration: Configuration) = synchronizer {
        database.reload(configuration)
        database.addOnResourceLoadListener(callback)
        generateJob(configuration)
        weakContext = WeakReference(context)
    }

    override fun addImageChangeListener(callback: WallpaperProvider.Callback): Boolean = listener.add(callback)

    override fun removeImageChangeListener(callback: WallpaperProvider.Callback): Boolean = listener.remove(callback)

    override fun surfaceCreated(holder: SurfaceHolder) = synchronizer {
        this.holder = holder
    }

    override fun surfaceChanged(holder: SurfaceHolder) = synchronizer {
        this.holder = holder
        val (height: Int, width: Int) = this.holder?.surfaceFrame?.run { height() to width() } ?: return@synchronizer

        database.load(width, height, isPortrait, true)
    }

    override fun surfaceDestroyed() = synchronizer { holder = null }

    private fun synchronizer(completion: () -> Unit) {
        synchronized(locker) {
            completion()
        }
    }

    private fun generateJob(configuration: Configuration) {
        job = coroutineScope.launch {
            while (true) {
                delay(configuration.duration)
                next()
            }
        }
    }

    private fun draw() {
        if (!isVisible || animationIsRunning)
            return

        animationIsRunning = true
        mainHandler.post {
            objectAnimator.start()
        }
    }

    private fun onDraw() {
        val surfaceHolder = holder ?: return
        val current = currentImage ?: return

        val canvas = surfaceHolder.lockCanvas()
        try {
            canvas.drawColor(Color.BLACK, BlendMode.CLEAR)
            lastImage?.bitmap?.let { canvas.drawBitmap(it, 0f, 0f, lastPaint) }
            canvas.drawBitmap(current.bitmap, 0f, 0f, paint)
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

}