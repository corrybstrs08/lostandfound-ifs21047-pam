package com.ifs21047.lostandfound.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifs21047.lostandfound.data.local.entity.LostFoundEntity

@Dao
interface IDelcomLostFoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(delcomTodo: LostFoundEntity)
    @Delete
    fun delete(delcomLostFound: LostFoundEntity)
    @Query("SELECT * FROM delcom_lostfounds WHERE id = :id LIMIT 1")
    fun get(id: Int): LiveData<LostFoundEntity?>
    @Query("SELECT * FROM delcom_lostfounds ORDER BY created_at DESC")
    fun getAllLostFounds(): LiveData<List<LostFoundEntity>?>
}