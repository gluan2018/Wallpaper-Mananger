package br.com.yuki.wallpaper.manager.implementations

import android.graphics.*
import android.util.Size
import androidx.core.graphics.applyCanvas
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.album.AlbumNotSet
import br.com.yuki.wallpaper.manager.database.model.image.EmptyImage
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.database.service.WallpaperService
import br.com.yuki.wallpaper.manager.model.Configuration
import br.com.yuki.wallpaper.manager.service.provider.BitmapProvider
import br.com.yuki.wallpaper.manager.service.provider.DatabaseProvider
import br.com.yuki.wallpaper.manager.service.provider.helper.SurfaceImage
import br.com.yuki.wallpaper.manager.util.global.utility.size
import br.com.yuki.wallpaper.manager.util.tools.TransformationUtil
import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class DataProviderImpl(
    private val bitmapProvider: BitmapProvider,
    private val wallpaperService: WallpaperService
) : DatabaseProvider {

    companion object {
        private const val PAGE_SIZE = 50
    }

    private val coroutineScope = CoroutineScope(SupervisorJob())
    private var job: Job? = null

    private var callback: MutableList<DatabaseProvider.Callback> = mutableListOf()

    private var locker = ReentrantLock()

    private var currentConfiguration: Configuration? = null
    private var currentAlbum: Album? = null

    private var pages: MutableList<Image> = mutableListOf()
    private var currentIndex = 0
    private var currentOffset = 0
    private var currentSize = 0

    private val paintImage = Paint()

    override val configuration: Configuration?
        get() = currentConfiguration

    override fun reload(configuration: Configuration) {
        cancel()

        synchronizer {
            currentConfiguration = configuration
            val albumId = configuration.albumId ?: return@synchronizer

            currentAlbum = suspend { wallpaperService.findAlbum(albumId) }.invoke()
            currentSize = suspend { wallpaperService.countImage(albumId) }.invoke()
        }
    }

    override fun cancel() {
        job?.cancel()
        job = null

        pages.clear()
        currentIndex = 0
        currentOffset = 0
        currentSize = 0
    }

    override fun load(width: Int, height: Int, isPortrait: Boolean, isReload: Boolean) {
        if (job?.isActive == true)
            job?.cancel()
        job = synchronizer {
            val album = currentAlbum ?: run {
                callback.forEach { result ->
                    result.resource(
                        SurfaceImage(album = AlbumNotSet(), image = EmptyImage(), bitmap = infoNotSetAlbum(width, height))
                    )
                }
                return@synchronizer
            }
            page(album.id)?.let { image ->
                val file = image.path?.let { path -> File(path) } ?: return@synchronizer
                runCatching {
                    val bitmap = bitmapProvider.bitmap(file, width, height)
                    SurfaceImage(album = album, image = image, bitmap = prepareImage(image, width, height, isPortrait, bitmap))
                }.onSuccess { result ->
                    callback.forEach { call ->
                        call.resource(result)
                    }
                }
            }
        }
    }

    override fun loadLockScreen(width: Int, height: Int, isPortrait: Boolean, isReload: Boolean) {

    }

    override fun addOnResourceLoadListener(listener: DatabaseProvider.Callback) {
        callback.add(listener)
    }

    override fun removeOnResourceLoadListener(listener: DatabaseProvider.Callback) {
        callback.remove(listener)
    }

    private suspend fun page(albumId: Long): Image? {
        return when {
            currentSize == 0 -> null
            currentIndex <= pages.lastIndex -> pages[currentIndex++]
            currentOffset.plus(1).times(PAGE_SIZE) < currentSize -> {
                currentIndex = 0
                val pageable = suspend { wallpaperService.pageable(albumId, ++currentOffset, PAGE_SIZE) }.invoke()
                pages.clear()
                pages.addAll(pageable)

                if (configuration?.shuffleAlbum == true)
                    pages.shuffle()

                pages[currentIndex++]
            }
            else -> {
                currentOffset = 0
                currentIndex = 0
                val pageable = suspend { wallpaperService.pageable(albumId, currentOffset, PAGE_SIZE) }.invoke()
                pages.clear()
                pages.addAll(pageable)

                if (configuration?.shuffleAlbum == true)
                    pages.shuffle()

                pages[currentIndex++]
            }
        }
    }

    private fun synchronizer(completion: suspend () -> Unit) = coroutineScope.launch(Dispatchers.IO) {
        try {
            locker.tryLock(1, TimeUnit.MINUTES)
            completion()
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            locker.unlock()
        }
    }

    private fun prepareImage(image: Image, width: Int, height: Int, isPortrait: Boolean, current: Bitmap): Bitmap {
        val portrait = image.vertical
        val landscape = image.horizontal

        val size = Size(width, height)

        val matrixData = when {
            portrait != null && isPortrait -> TransformationUtil.makeMatrix(current.size, size, portrait)
            landscape != null && !isPortrait -> TransformationUtil.makeMatrix(current.size, size, landscape)
            else -> TransformationUtil.centerCrop(current.size, size)
        }

        paintImage.reset()
        return Bitmap.createBitmap(width, height, current.config).applyCanvas {
            configuration?.let { values ->
                paintImage.alpha = 255 - values.alpha
            }
            drawBitmap(current, matrixData.matrix, paintImage)
        }
    }

    private fun infoNotSetAlbum(width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565).applyCanvas {
            drawColor(Color.BLACK)
            infoPaint()
        }
    }

    private fun Canvas.infoPaint(): Paint {
        val text = "Album not found"
        val rect = Rect()
        return Paint().apply {
            setARGB(255, 255, 255, 255)
            textAlign = Paint.Align.CENTER
            textSize = 24f
            getTextBounds(text, 0, text.length, rect)

            val x: Float = this@infoPaint.width / 2f - rect.width() / 2f - rect.left
            val y: Float = this@infoPaint.height / 2f + rect.height() / 2f - rect.bottom

            this@infoPaint.drawText(text, x, y, this)
        }
    }

}