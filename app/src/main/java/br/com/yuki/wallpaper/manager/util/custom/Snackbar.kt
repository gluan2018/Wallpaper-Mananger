package br.com.yuki.wallpaper.manager.util.custom

import android.graphics.Color
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.util.global.OnClickListener
import br.com.yuki.wallpaper.manager.util.global.utility.pixel
import com.google.android.material.snackbar.Snackbar

fun Snackbar.successful(
    action: String? = null,
    callback: OnClickListener<Boolean>? = null
): Snackbar {
    if (action != null)
        this.setAction(action) {
            callback?.onClick(true)
        }

    this.setTextColor(Color.BLACK)
    this.setBackgroundTint(Color.WHITE)
    this.setActionTextColor(Color.parseColor("#ED1E24"))
    return this
}

fun Snackbar.error(
    retry: OnClickListener<Boolean>? = null
): Snackbar {
    if (retry != null)
        this.setAction(R.string.retry) {
            retry.onClick(true)
        }

    this.setTextColor(Color.BLACK)
    this.setBackgroundTint(Color.WHITE)
    this.setActionTextColor(Color.parseColor("#ED1E24"))
    return this
}

fun Snackbar.margin(
    left: Int = 32f.pixel,
    top: Int = 32f.pixel,
    right: Int = 32f.pixel,
    bottom: Int = 32f.pixel
): Snackbar {
    view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        setMargins(
            left, top, right, bottom
        )
    }
    show()
    return this
}