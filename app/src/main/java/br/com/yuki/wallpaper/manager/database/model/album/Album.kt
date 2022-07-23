package br.com.yuki.wallpaper.manager.database.model.album

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "album"
)
open class Album(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "paste") var paste: String
) : Parcelable