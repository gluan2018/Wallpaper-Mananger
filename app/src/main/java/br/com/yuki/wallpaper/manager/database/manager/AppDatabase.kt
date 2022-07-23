package br.com.yuki.wallpaper.manager.database.manager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.database.service.AlarmInterface
import br.com.yuki.wallpaper.manager.database.service.WallpaperService
import br.com.yuki.wallpaper.manager.model.Alarm

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        Album::class,
        Alarm::class,
        Image::class
    ],
)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, "AppDatabase.db")
                .fallbackToDestructiveMigration()
                .build()
        }

    }

    abstract fun wallpaper(): WallpaperService

    abstract fun alarm(): AlarmInterface

}