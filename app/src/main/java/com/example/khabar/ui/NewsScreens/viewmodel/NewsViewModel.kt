package com.example.khabar.ui.NewsScreens.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khabar.data.model.NewsResponse
import com.example.khabar.data.repository.NewsRepo
import com.example.khabar.utilis.AppConstants
import com.example.khabar.utils.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class NewsViewModel @Inject constructor(private val newsRepo: NewsRepo): ViewModel(){
    private val _news = MutableStateFlow<ResourceState<NewsResponse>>(ResourceState.Loading())
    val news : StateFlow<ResourceState<NewsResponse>> = _news

    init{
        getNews(AppConstants.COUNTRY)
    }
    private fun getNews(country:String){
        viewModelScope.launch(Dispatchers.IO) {
            newsRepo
                .getNewsHeadline(country)
                .collectLatest { newsResponse ->
                    Log.d(TAG,"Received news response: $newsResponse")
                    _news.value = newsResponse
                }
        }
    }
}