package br.com.yuki.wallpaper.manager.database.manager

import android.graphics.PointF
import androidx.room.TypeConverter
import br.com.yuki.wallpaper.manager.database.model.image.Image
import com.google.gson.Gson

class ConverterUtil {

    @TypeConverter
    fun infoToString(info: Image.Info?): String? = info?.run { Gson().toJson(this) }

    @TypeConverter
    fun stringToInfo(string: String?): Image.Info? = string?.run { Gson().fromJson(this, Image.Info::class.java) }

    @TypeConverter
    fun pointToString(point: PointF?): String? = point?.run { Gson().toJson(this) }

    @TypeConverter
    fun stringToPoint(string: String?): PointF? = string?.run { Gson().fromJson(this, PointF::class.java) }

}