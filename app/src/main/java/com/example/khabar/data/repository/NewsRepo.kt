package com.example.khabar.data.repository

import com.example.khabar.data.datasource.NewsDatasource
import com.example.khabar.data.model.NewsResponse
import com.example.khabar.utils.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepo @Inject constructor(private val newsDatasource: NewsDatasource){
    fun getNewsHeadline(country: String): Flow<ResourceState<NewsResponse>>{
        return flow{
            emit(ResourceState.Loading())
            val response = newsDatasource.getNewsHeadlines(country)
            if(response.isSuccessful && response.body() != null){
                emit(ResourceState.Success(response.body()!!))
            }else{
                emit(ResourceState.Error(response.message()))
            }
        }
    }
}