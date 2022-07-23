package br.com.yuki.wallpaper.manager.util.global.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutManager
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.screen.home.HomeActivity
import java.util.*

class Shortcut(
    private val context: Context
) {

    private val manager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager

    val hasShortcut: Boolean
        get() = ShortcutManagerCompat.getDynamicShortcuts(context).isNotEmpty()

    fun setShortcuts() {
        val shortcut = ShortcutInfoCompat.Builder(context, UUID.randomUUID().toString())
            .setShortLabel(context.getString(R.string.change))
            .setLongLabel(context.getString(R.string.change))
            .setIntent(
                Intent(Intent.ACTION_VIEW)
                    .putExtra("ChangeAlbum", "")
            )
            .setActivity(ComponentName(context, HomeActivity::class.java))
            .build()
        ShortcutManagerCompat
            .pushDynamicShortcut(context, shortcut)
    }

}