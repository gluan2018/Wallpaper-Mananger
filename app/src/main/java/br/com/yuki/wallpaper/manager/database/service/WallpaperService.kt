package br.com.yuki.wallpaper.manager.database.service

import androidx.room.*
import br.com.yuki.wallpaper.manager.database.model.album.Album
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.database.model.image.relation.ImageAlbum

@Dao
interface WallpaperService {

    @Insert
    fun add(album: Album): Long

    @Delete
    fun delete(album: Album)

    @Update
    fun update(album: Album)

    @Query("select * from image where albumId = :albumId order by position")
    fun all(albumId: Long): List<Image>

    @Query("select * from album where id = :albumId")
    fun findAlbum(albumId: Long): Album?

    @Query("select * from album")
    fun allAlbum(): List<Album>

    @Query("select * from image where albumId = :albumId order by position limit :limit offset :offset")
    fun pageable(albumId: Long, offset: Int, limit: Int): List<Image>

    @Query("select * from image")
    fun all(): List<Image>

    @Transaction
    @Throws
    fun findAlbumWithImage(albumId: Long): ImageAlbum {
        val album = findAlbum(albumId) ?: throw Throwable()
        return ImageAlbum(
            album = album,
            firstImage = findFirstImageInAlbum(albumId)
        )
    }

    @Transaction
    fun imageAlbum(): List<ImageAlbum> {
        return allAlbum().map { album ->
            ImageAlbum(
                album = album,
                firstImage = findFirstImageInAlbum(album.id)
            )
        }
    }

    @Query("select * from image where albumId = :albumId order by position asc")
    fun findFirstImageInAlbum(albumId: Long): Image?

    @Query("select * from image where id = :id")
    fun findImage(id: Long): Image?

    @Query("select count() from image where albumId = :albumId")
    fun countImage(albumId: Long): Int

    @Insert
    fun add(image: Image): Long

    @Update
    fun update(image: Image)

    @Delete
    fun delete(image: Image)

}