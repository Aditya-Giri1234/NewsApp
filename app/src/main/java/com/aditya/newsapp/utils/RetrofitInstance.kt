package com.aditya.newsapp.utils

import com.aditya.newsapp.apiService.NewsApi
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private var INSTANCE:Retrofit?=null
    private fun getRetrofit():Retrofit{
        if(INSTANCE==null){
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client=OkHttpClient.Builder().addInterceptor(logging).build()
            INSTANCE=Retrofit.Builder().baseUrl(Constant.base_url).addConverterFactory(GsonConverterFactory.create()).build()
        }
        return INSTANCE!!
    }
    fun getApiService():NewsApi{
        return getRetrofit().create(NewsApi::class.java)
    }
}