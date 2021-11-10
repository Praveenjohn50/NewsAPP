package com.example.newsreader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.newsreader.database.NewsRepository
import com.example.newsreader.models.Article


import kotlinx.coroutines.launch



class NewsViewModel : ViewModel() {


    init {
        getBreakingNews("us")
    }


    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            NewsRepository.getBreakingNews(countryCode)
        }
    }

    fun addFavourite(context: Context, article: Article) {
        viewModelScope.launch {
            NewsRepository.addFavourite(context, article)
        }
    }


    fun deleteArticle(context: Context,article: Article) {
        viewModelScope.launch {
            NewsRepository.removeFavourite(context, article)
        }
    }


    fun getFavouriteNews(context: Context) = NewsRepository.getFavoutiteNews(context)

}