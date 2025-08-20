package com.example.khabar.data.datasource

import com.example.khabar.data.api.ApiServices
import com.example.khabar.data.model.NewsResponse
import retrofit2.Response
import javax.inject.Inject

class NewsDatasourceImpl @Inject constructor(private val apiServices: ApiServices): NewsDatasource {
    override suspend fun getNewsHeadlines(country: String): Response<NewsResponse> {
        return apiServices.getNewsHeadlines(country)
    }
}