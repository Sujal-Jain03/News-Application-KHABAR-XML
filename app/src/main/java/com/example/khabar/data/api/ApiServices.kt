package com.example.khabar.data.api

import com.example.khabar.data.model.NewsResponse

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("v2/top-headlines")
    suspend fun getNewsHeadlines(
    @Query("country") country:String,
    @Query("apiKey") apiKey:String = "a75cb35e10d24e8aaf13542ee05fa5c1"
    ): Response<NewsResponse>
}

