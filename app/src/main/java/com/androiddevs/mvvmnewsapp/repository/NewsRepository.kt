package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase

class NewsRepository(val db: ArticleDatabase){

    suspend fun getBreakingNews(country: String, page: Int) =
        RetrofitInstance.api.getBreakingNews(country, page)

    suspend fun getSearchNews(country: String, page: Int) =
        RetrofitInstance.api.getSearchNews(country, page)
}