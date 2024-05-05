package com.aditya.newsapp.apiService

import com.aditya.newsapp.model.NewsResponse
import com.aditya.newsapp.utils.Constant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET(Constant.breaking)
    suspend fun getBreakingNews(
        @Query(Constant.country) country:String="us",
        @Query(Constant.page) page:Int=1,
        @Query(Constant.api_key) apiKey:String=Constant.API_KEY
    ):Response<NewsResponse>
    @GET(Constant.search)
    suspend fun getSearchNews(
        @Query(Constant.query) query:String,
        @Query(Constant.page) page:Int=1,
        @Query(Constant.api_key) apiKey:String=Constant.API_KEY
    ):Response<NewsResponse>
}