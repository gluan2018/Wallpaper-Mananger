package br.com.yuki.wallpaper.manager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Configuration(
    var albumId: Long?,
    var timer: Duration,
    var shuffleAlbum: Boolean,
    var alpha: Int,
    var doubleClick: Boolean,
    var changeWhenLock: Boolean
) : Parcelable {

    companion object {

        val DEFAULT: Configuration
            get() = Configuration(
                albumId = 2,
                timer = Duration(
                    minutes = 1,
                    hours = 0
                ),
                shuffleAlbum = true,
                alpha = 0,
                doubleClick = true,
                changeWhenLock = false
            )

    }

    @Parcelize
    data class Duration(
        var minutes: Long,
        var hours: Long,
    ) : Parcelable

    val duration: Long
        get() = timer.minutes.times(60000) +
                timer.hours.times(3600000)

    override fun equals(other: Any?): Boolean {
        if (other !is Configuration)
            return false
        return albumId == other.albumId &&
                duration == other.duration &&
                shuffleAlbum == other.shuffleAlbum &&
                alpha == other.alpha &&
                doubleClick == other.doubleClick
    }

    override fun hashCode(): Int {
        var result = albumId ?: 0
        result = 31 * result + duration.hashCode()
        result = 31 * result + shuffleAlbum.hashCode()
        result = 31 * result + alpha.hashCode()
        result = 31 * result + doubleClick.hashCode()
        return result.toInt()
    }

}
