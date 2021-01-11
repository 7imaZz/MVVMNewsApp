package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.pojo.Article
import com.androiddevs.mvvmnewsapp.pojo.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class NewsViewModel(private val application: Application, private val newsRepository: NewsRepository): ViewModel() {

    val breakingNewsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsResponse? = null

    val searchLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchPage = 1
    private var searchingNewsResponse: NewsResponse? = null


    init {
        getBreakingNews("eg")
    }

    fun getBreakingNews(country: String) = viewModelScope.launch {
        if (isNetworkAvailable(application)) {
            breakingNewsLiveData.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(country, breakingNewsPage)
            breakingNewsLiveData.postValue(handleBreakingNewsResponse(response))
        }else{
            Toast.makeText(application, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse->
                breakingNewsPage++
                if (breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                }else{
                    val oldArticles: MutableList<Article> = breakingNewsResponse?.articles as MutableList<Article>
                    val newArticles = resultResponse.articles
                    oldArticles.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.body(), response.message())
    }

    fun getSearchNews(query: String) = viewModelScope.launch {
        if (isNetworkAvailable(application)) {
            searchLiveData.postValue(Resource.Loading())
            val response = newsRepository.getSearchNews(query, searchPage)
            searchLiveData.postValue(handleSearchNewsResponse(response))
        }else{
            Toast.makeText(application, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse->
                searchPage++
                if (searchingNewsResponse == null){
                    searchingNewsResponse = resultResponse
                }else{
                    val oldArticles: MutableList<Article> = searchingNewsResponse?.articles as MutableList<Article>
                    val newArticles = resultResponse.articles
                    oldArticles.addAll(newArticles)
                }
                return Resource.Success(searchingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.body(), response.message())
    }

    fun insertArticle(article: Article) = viewModelScope.launch {
        newsRepository.insertArticle(article)
    }

    fun getAllArticles() = newsRepository.getAllArticles()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private fun isNetworkAvailable(application: Application): Boolean {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw)
            actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(
                NetworkCapabilities.TRANSPORT_BLUETOOTH
            ))
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo
            nwInfo != null && nwInfo.isConnected
        }
    }
}