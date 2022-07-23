package br.com.yuki.wallpaper.manager.database.model.image

import android.graphics.PointF
import android.os.Parcelable
import androidx.room.*
import br.com.yuki.wallpaper.manager.database.manager.ConverterUtil
import br.com.yuki.wallpaper.manager.database.model.album.Album
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "image",
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["albumId"]
        )
    ]
)
@Parcelize
@TypeConverters(ConverterUtil::class)
open class Image(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "albumId", index = true) val albumId: Long,
    @ColumnInfo(name = "path") var path: String?,
    @ColumnInfo(name = "horizontal") var horizontal: Info? = null,
    @ColumnInfo(name = "vertical") var vertical: Info? = null,
    @ColumnInfo(name = "position", index = true) var index: Int
) : Parcelable {

    @Parcelize
    data class Info(
        val zoom: Float = 1f,
        val point: PointF = PointF()
    ) : Parcelable

}
