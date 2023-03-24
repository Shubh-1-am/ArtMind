package com.example.artmind.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.artmind.repository.OpenAiRepository
import com.example.artmind.retrofit.RetrofitHelper

class OpenAiViewModel : ViewModel() {

    private val openAiRepository = OpenAiRepository(RetrofitHelper.apiService)

    suspend fun generateImages(prompt: String, n: Int?, size: String?): LiveData<ArrayList<String>> {
        openAiRepository.generateImages(prompt, n, size)
        return openAiRepository.generatedImages
    }
}

