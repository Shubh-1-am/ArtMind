package com.example.artmind.repository

import androidx.lifecycle.LiveData
import com.example.artmind.dao.ImageDao
import com.example.artmind.database.ImageDatabase
import com.example.artmind.modal.ImageEntity
import java.io.File

class ImageRepository(private val imageDatabase: ImageDatabase) {
    private val imageDao: ImageDao = imageDatabase.imageDao()
    fun getLast20Images(): LiveData<List<ImageEntity>> = imageDao.getLast20Images()


    suspend fun insertImage(imagePath: String) {
            val imageEntity = ImageEntity(imagePath = imagePath, timestamp = System.currentTimeMillis())
            val images = imageDao.getAllImages()
            if ( images.size >= 20) {
                val oldestImage = images.last()
                imageDao.deleteImage(oldestImage.id)
                val file = File(oldestImage.imagePath)
                file.delete()
            }
            imageDao.insertImage(imageEntity)
    }
}
