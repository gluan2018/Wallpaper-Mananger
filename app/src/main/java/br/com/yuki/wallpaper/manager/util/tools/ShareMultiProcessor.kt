package br.com.yuki.wallpaper.manager.util.tools

import android.content.Context
import android.os.FileObserver
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.model.Configuration
import br.com.yuki.wallpaper.manager.util.global.OnClickListener
import com.google.gson.Gson
import java.io.File

private const val FILE_CONFIGURATIONS = "configurations.json"
private const val FILE_UPDATE = "update.json"

interface Preferences<T> {
    val isWatching: Boolean

    var current: T?

    fun startObserver()
    fun removeObserver()
    fun setCallback(callback: OnClickListener<T>)

    companion object {

        fun image(context: Context): Preferences<Image> {
            val file = File(context.applicationContext.filesDir, FILE_UPDATE).apply {
                if (!exists())
                    createNewFile()
            }

            return SamplePreferences(
                file = file,
                clazz = Image::class.java
            )
        }

        fun config(context: Context): Preferences<Configuration> {
            val file = File(context.applicationContext.filesDir, FILE_CONFIGURATIONS).apply {
                if (!exists())
                    createNewFile()
            }

            return SamplePreferences(
                file = file,
                clazz = Configuration::class.java
            )
        }
    }

}

private class SamplePreferences<T>(
    private val file: File,
    private val clazz: Class<T>
) : Preferences<T> {

    private val gson = Gson()
    private var callback: OnClickListener<T>? = null

    private val fileObserver = object : FileObserver(file, CLOSE_WRITE) {
        override fun onEvent(p0: Int, p1: String?) {
            current?.apply {
                callback?.onClick(this)
            }
        }
    }

    override var isWatching: Boolean = false
        private set

    override var current: T?
        get() = runCatching {
            if (file.length() == 0L)
                return@runCatching null

            return@runCatching file.inputStream().use { input ->
                gson.fromJson(input.reader(), clazz)
            }
        }.getOrNull()
        set(value) {
            runCatching {
                file.outputStream().use { output ->
                    if (value == null)
                        output.write(byteArrayOf())
                    else
                        output.write(gson.toJson(value).toByteArray())
                }
            }
        }

    override fun setCallback(callback: OnClickListener<T>) {
        this.callback = callback
    }

    override fun startObserver() {
        fileObserver.startWatching()
        isWatching = true
    }

    override fun removeObserver() {
        fileObserver.stopWatching()
        isWatching = false
    }

}

fun Preferences<Configuration>.getOrDefault(): Configuration = current ?: Configuration.DEFAULT