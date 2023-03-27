package com.example.artmind.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.artmind.BuildConfig

import com.example.artmind.retrofit.OpenAiApi
import com.example.artmind.retrofit.RequestBody


class OpenAiRepository(private val apiService: OpenAiApi) {

    private val _generatedImages = MutableLiveData<ArrayList<String>>()
    val generatedImages: LiveData<ArrayList<String>> = _generatedImages

    suspend fun generateImages(prompt: String, n: Int?, size: String?) {
        val requestBody = RequestBody(prompt, n, size)
        val response = apiService.generateImages(BuildConfig.OPENAI_API_KEY, requestBody)
        if (response.isSuccessful && response.body() != null) {
            val data = response.body()?.data
            val urls = data?.map { it.url } as ArrayList<String>?
            _generatedImages.postValue(urls)
        } else {
            // handle the API request failure
            Log.e("OpenAiRepository", "API request failed with code ${response.code()}")
        }
    }
}