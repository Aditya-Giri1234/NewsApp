package com.aditya.newsapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aditya.newsapp.R
import com.aditya.newsapp.databinding.ActivityNewsBinding
import com.aditya.newsapp.db.ArticleDatabase
import com.aditya.newsapp.repository.NewsRepository
import com.aditya.newsapp.viewModel.NewsViewModel
import com.aditya.newsapp.viewModelFactory.NewsViewModelFactory

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    val viewModel by lazy {
        val newsRepository=NewsRepository(ArticleDatabase(this))
        ViewModelProvider(this , NewsViewModelFactory(application , newsRepository))[NewsViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setupWithNavController(findNavController(R.id.navHostFragment))
    }
}