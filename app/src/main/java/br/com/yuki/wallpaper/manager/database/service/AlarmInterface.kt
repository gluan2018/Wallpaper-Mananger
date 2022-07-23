package br.com.yuki.wallpaper.manager.database.service

import androidx.room.*
import br.com.yuki.wallpaper.manager.model.Alarm

@Dao
interface AlarmInterface {

    @Query("select * from alarm order by id")
    fun getAll(): List<Alarm>

    @Insert
    fun insert(alarm: Alarm): Long

    @Update
    fun update(alarm: Alarm)

    @Delete
    fun delete(alarm: Alarm)

}