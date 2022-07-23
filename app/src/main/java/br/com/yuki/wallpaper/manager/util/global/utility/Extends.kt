package br.com.yuki.wallpaper.manager.util.global.utility

import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Size
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import br.com.yuki.wallpaper.manager.screen.home.HomeActivity
import kotlin.math.roundToInt

/* View */

val View.inflater: LayoutInflater
    get() = LayoutInflater.from(this.context)

/* Primitive */

val Number.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

val Number.pixel: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()

/* Others */
val Fragment.statusBarHeight: Int
    get() {
        val homeActivity = activity as? HomeActivity
        return homeActivity?.topInset ?: 0
    }

@Suppress("unchecked_cast")
fun <T> Any.cast() = this as T

val Bitmap.size: Size
    get() = Size(width, height)