package com.aditya.newsapp.apiService

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aditya.newsapp.model.Article

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long

    @Query("select * from articles")
    fun getAllArticle():LiveData<List<Article>>

    @Delete
    suspend fun delete(article: Article)

    @Query("select * from articles where url=:artUrl")
    suspend fun getArticleByUrl(artUrl:String):Article?
}