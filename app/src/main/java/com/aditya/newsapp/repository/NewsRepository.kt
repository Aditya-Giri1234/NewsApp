package com.aditya.newsapp.repository

import com.aditya.newsapp.db.ArticleDatabase
import com.aditya.newsapp.utils.RetrofitInstance

class NewsRepository (val db:ArticleDatabase){

    suspend fun getBreakingNews(countryCode:String, pageNumber:Int)=
        RetrofitInstance.getApiService().getBreakingNews(countryCode,pageNumber)

    suspend fun getSearchNews(searchQuery:String, pageNumber:Int)=
        RetrofitInstance.getApiService().getSearchNews(searchQuery,pageNumber)

}