package com.example.artmind.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.artmind.modal.ImageEntity

@Dao
interface ImageDao {
    @Query("SELECT * FROM images ORDER BY timestamp DESC LIMIT 20")
    fun getLast20Images(): LiveData<List<ImageEntity>>

    @Query("SELECT * FROM images ORDER BY timestamp DESC")
    suspend fun getAllImages(): List<ImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Query("DELETE FROM images WHERE id=:id")
    suspend fun deleteImage(id: Int)
}
