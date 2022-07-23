package br.com.yuki.wallpaper.manager.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.yuki.wallpaper.manager.database.manager.AppDatabase
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.database.service.WallpaperService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class TestImage {

    private lateinit var database: AppDatabase
    private lateinit var service: WallpaperService

    @Before
    fun createDatabase() {
        database = ApplicationProvider.getApplicationContext<Context>().run {
            Room.inMemoryDatabaseBuilder(this, AppDatabase::class.java).build()
        }
        service = database.wallpaper()
    }

    @After
    fun close() {
        database.close()
    }

    @Test
    fun writeImage() {
        val album = Album(id = 0, name = "Test", paste = "none").apply {
            this.id = service.add(this)
        }

        for (position in 0..Random.nextInt(5, 10)) {
            val image = Image(albumId = album.id, path = null, horizontal = Image.Info(), vertical = null, index = position).apply {
                this.id = service.add(this)
            }
            logger(image)
            service.update(image.apply { path = "updated" })
        }

        logger(service.all())
        logger(service.imageAlbum())
        logger(service.pageable(album.id, 0, 10))
    }

    private fun logger(data: Any?) = Log.d("T", data?.toString() ?: "")

}