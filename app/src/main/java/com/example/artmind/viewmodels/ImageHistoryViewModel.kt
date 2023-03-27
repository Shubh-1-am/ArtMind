package com.example.artmind.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.artmind.database.ImageDatabase
import com.example.artmind.modal.ImageEntity
import com.example.artmind.repository.ImageRepository

class ImageHistoryViewModel(application: Application) : ViewModel(),java.io.Serializable {
    private val imageRepository: ImageRepository

    init {
        val imageDatabase = ImageDatabase.getInstance(application)
        imageRepository = ImageRepository(imageDatabase)
    }
    val imagesLiveData: LiveData<List<ImageEntity>> = imageRepository.getLast20Images()

    suspend fun insertImage(imagePath: String) {
        imageRepository.insertImage(imagePath)
    }
}
