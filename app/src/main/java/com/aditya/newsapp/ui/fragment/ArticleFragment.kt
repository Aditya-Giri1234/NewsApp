package com.aditya.newsapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.aditya.newsapp.R
import com.aditya.newsapp.databinding.FragmentArticleBinding
import com.aditya.newsapp.ui.activity.NewsActivity
import com.aditya.newsapp.utils.Helper
import com.aditya.newsapp.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment() {

    val TAG = "ArticleFragment"
    lateinit var binding: FragmentArticleBinding
    val viewModel by lazy {
        (activity as NewsActivity).viewModel
    }
    private val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        val article = args.article
        Helper.customLog("WebView", "$TAG - initUi - article->$article")
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url!!)
        }

        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            view?.let { it1 ->
                Snackbar.make(
                    it1,
                    "Article saved Successfully !",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
}