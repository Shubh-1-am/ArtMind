package com.example.artmind.retrofit

import com.example.artmind.modal.Data
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiApi {
    @Headers("Content-Type: application/json")
    @POST("images/generations")
    suspend fun generateImages(
        @Header("Authorization") apiKey: String,
        @Body requestBody: RequestBody
    ): Response<Data>
}

data class RequestBody(
    val prompt: String,
    val n: Int? = null,
    val size: String? = null
)