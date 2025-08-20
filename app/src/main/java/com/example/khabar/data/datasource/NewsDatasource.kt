package com.example.khabar.data.datasource

import com.example.khabar.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET

interface NewsDatasource {
    @GET("v2/top-headlines")
    suspend fun getNewsHeadlines(
        country: String,
    ): Response<NewsResponse>
}