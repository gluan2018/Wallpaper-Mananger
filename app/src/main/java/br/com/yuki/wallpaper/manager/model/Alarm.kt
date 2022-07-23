package br.com.yuki.wallpaper.manager.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.yuki.wallpaper.manager.database.model.album.Album
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "alarm",
    foreignKeys = [
        ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
@Parcelize
data class Alarm(
    @PrimaryKey
    @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "hour") var hour: Int,
    @ColumnInfo(name = "minute") var minute: Int,
    @ColumnInfo(name = "days") var days: Int,
    @ColumnInfo(name = "albumId", index = true) var albumId: Long?,
    @ColumnInfo(name = "albumName") var albumName: String?,
    @ColumnInfo(name = "isEnable") var isEnable: Boolean
) : Parcelable {

    companion object {

        val default: Alarm
            get() = Alarm(
                id = 0,
                hour = 8,
                days = 0,
                minute = 0,
                albumId = null,
                albumName = null,
                isEnable = false
            )
    }

    fun copyFrom(other: Alarm) {
        hour = other.hour
        days = other.days
        albumId = other.albumId
        minute = other.minute
        isEnable = other.isEnable
        albumName = other.albumName
    }

}