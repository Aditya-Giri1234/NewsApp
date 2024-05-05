package com.aditya.newsapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.newsapp.model.Article
import com.aditya.newsapp.model.NewsResponse
import com.aditya.newsapp.repository.NewsRepository
import com.aditya.newsapp.utils.Helper
import com.aditya.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(private val app:Application , private val newsRepository: NewsRepository) : AndroidViewModel(app) {
    val TAG = "NewsViewModel"

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingPageNumber = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
     var searchingPageNumber = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("in")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading<NewsResponse>())
        try {
            if(Helper.isInternetAvailable(app)){
                val response = newsRepository.getBreakingNews(countryCode, breakingPageNumber)
                breakingNews.postValue(handleBreakingNews(response))
            }
            else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        }
        catch (t :Throwable){
            when(t){
                is IOException->{
                    breakingNews.postValue(Resource.Error("Network Failure"))
                }
                else->{
                    breakingNews.postValue(Resource.Error("Conversion Error "))
                }
            }
        }

    }

    private fun handleBreakingNews(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                Helper.customLog(
                    "BreakingNews",
                    "$TAG - handleBreakingNews - response->${newsResponse.articles} - response->$response"
                )
                breakingPageNumber++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = newsResponse
                } else {
                    val oldArticle= breakingNewsResponse?.articles
                    val newArticle = newsResponse.articles
                    oldArticle?.addAll(newArticle)
                }

                return Resource.Success(breakingNewsResponse ?: newsResponse)
            }
        }
        Helper.customLog(
            "BreakingNews",
            "$TAG - handleBreakingNews -  response->$response"
        )
        return Resource.Error(response.message())
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading<NewsResponse>())
        try {
            if(Helper.isInternetAvailable(app)){
                val response = newsRepository.getSearchNews(searchQuery, searchingPageNumber)
                searchNews.postValue(handleSearchNews(response))
            }
            else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }
        catch (t :Throwable){
            when(t){
                is IOException->{
                    searchNews.postValue(Resource.Error("Network Failure"))
                }
                else->{
                    searchNews.postValue(Resource.Error("Conversion Error "))
                }
            }
        }

    }

    private fun handleSearchNews(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {searchResponse->
                Helper.customLog(
                    "SearchNews",
                    "$TAG - handleSearchNews - response->${searchResponse.articles} - response->$response"
                )
                searchingPageNumber++
                if (searchNewsResponse == null) {
                    searchNewsResponse = searchResponse
                } else {
                    val oldArticle= searchNewsResponse?.articles
                    val newArticle = searchResponse.articles
                    oldArticle?.addAll(newArticle)
                }

                return Resource.Success(searchNewsResponse ?: searchResponse)
            }
        }
        Helper.customLog(
            "SearchNews",
            "$TAG - handleSearchNews -  response->$response"
        )
        return Resource.Error(response.message())
    }


    fun saveArticle(article: Article) = viewModelScope.launch {
        if (newsRepository.db.getDao().getArticleByUrl(article.url!!) == null) {
            val row = newsRepository.db.getDao().upsert(article)
            Helper.customLog("Insert_Row", "$TAG - saveArticle - rowId->$row")
        } else {
            Helper.customLog("Insert_Row", "$TAG - saveArticle - this article already saved !")
        }


    }

    fun getAllArticle() = newsRepository.db.getDao().getAllArticle()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.db.getDao().delete(article)
    }

}