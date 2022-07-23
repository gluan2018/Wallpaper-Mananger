package br.com.yuki.wallpaper.manager.util.global.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.album.relation.ResponseInsert
import br.com.yuki.wallpaper.manager.util.global.utility.GetContent.Callback

class GetContent : ActivityResultContract<Pair<Album, Array<String>>, GetContent.Callback?>() {

    private lateinit var album: Album

    fun interface Callback {
        suspend fun result(): ResponseInsert
    }

    override fun createIntent(context: Context, input: Pair<Album, Array<String>>): Intent {
        album = input.first
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            .setType("*/*")
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            .putExtra(Intent.EXTRA_MIME_TYPES, input.second)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Callback? {
        if (intent == null || resultCode != Activity.RESULT_OK)
            return null
        if (intent.clipData == null)
            return Callback {
                return@Callback ResponseInsert(
                    album =  album,
                    imagesUri = (intent.data?.run(::listOf) ?: listOf())
                )
            }
        return Callback {
            return@Callback ResponseInsert(
                album = album,
                imagesUri = intent.clipData!!.run {
                    (0 until this.itemCount).map { index ->
                        this.getItemAt(index).uri
                    }
                }
            )
        }
    }

}