package com.aditya.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aditya.newsapp.databinding.ItemArticlePreviewBinding
import com.aditya.newsapp.model.Article
import com.bumptech.glide.Glide

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    var differ=AsyncListDiffer(this, differCallback)
     var onItemClick:((Article)->Unit)?=null



    companion object{
        var differCallback=object : DiffUtil.ItemCallback<Article>(){
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url==newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem==newItem
            }
        }
    }

    fun submitList(list:List<Article>){
        differ.submitList(list)
    }


    inner class ViewHolder(val binding : ItemArticlePreviewBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder( ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article=differ.currentList[position]

        holder.binding.apply {
            Glide.with(holder.itemView).load(article.urlToImage).into(ivArticleImage)
            tvSource.text=article.source?.name
            tvTitle.text=article.title
            tvDescription.text=article.title
            tvPublishedAt.text=article.publishedAt
            root.setOnClickListener{
                onItemClick?.let { it(article) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}