package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.pojo.Article

class NewsRepository(private val db: ArticleDatabase){

    suspend fun getBreakingNews(country: String, page: Int) =
        RetrofitInstance.api.getBreakingNews(country, page)

    suspend fun getSearchNews(country: String, page: Int) =
        RetrofitInstance.api.getSearchNews(country, page)

    suspend fun insertArticle(article: Article) = db.ArticleDao().insertArticle(article)

    fun getAllArticles() = db.ArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.ArticleDao().deleteArticle(article)
}