package com.aditya.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.aditya.newsapp.apiService.NewsDao
import com.aditya.newsapp.model.Article

@Database(
    entities = [Article::class],
    version = 2
)
@TypeConverters(Converter::class)
abstract class ArticleDatabase :RoomDatabase() {

    abstract fun getDao(): NewsDao

    companion object{
        @Volatile
        var INSTANCE:ArticleDatabase?=null
        private val Lock=Any()

        operator  fun invoke(context: Context)= INSTANCE?: synchronized(Lock){
            createDatabase(context).also{
                INSTANCE=it
            }
        }
        private fun createDatabase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "Article.db"
            ).fallbackToDestructiveMigration().build()

    }

}