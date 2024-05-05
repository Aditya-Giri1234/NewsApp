package com.aditya.newsapp.viewModelFactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aditya.newsapp.repository.NewsRepository
import com.aditya.newsapp.viewModel.NewsViewModel

class NewsViewModelFactory (val app:Application , val newsRepository: NewsRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app , newsRepository ) as T
    }

}