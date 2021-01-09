package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.pojo.Article
import com.androiddevs.mvvmnewsapp.utils.OnItemClick
import com.bumptech.glide.Glide

class NewsAdapter(val onItemClick: OnItemClick): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var articleImage: ImageView = itemView.findViewById(R.id.ivArticleImage)
        var sourceTextView: TextView = itemView.findViewById(R.id.tvSource)
        var titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        var descTextView: TextView = itemView.findViewById(R.id.tvDescription)
        var publishTextView: TextView = itemView.findViewById(R.id.tvPublishedAt)
    }

    private val differCallback = object: DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        val article: Article = differ.currentList[position]

        Glide.with(holder.itemView)
            .load(article.urlToImage)
            .into(holder.articleImage)

        holder.titleTextView.text = article.title
        holder.sourceTextView.text = article.source.name
        holder.descTextView.text = article.description
        holder.publishTextView.text = article.publishedAt

        holder.itemView.setOnClickListener {
            onItemClick.onItemClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}