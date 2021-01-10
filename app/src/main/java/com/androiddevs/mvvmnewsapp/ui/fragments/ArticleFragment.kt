package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.pojo.Article
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article){

    lateinit var viewModel: NewsViewModel
    lateinit var allArticles: List<Article>

    private val args: ArticleFragmentArgs by navArgs()
    private var allUrls = mutableListOf<String?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel

        viewModel.getAllArticles().observe(viewLifecycleOwner, {
            allArticles = it

            for (mArticle in allArticles){
                allUrls.add(mArticle.url)
            }
        })


        val article = args.article

        webView.webViewClient = WebViewClient()
        article.url?.let { webView.loadUrl(it) }

        fab.setOnClickListener{
            if (allUrls.contains(article.url)){
                Snackbar.make(view, "Already Exist", Snackbar.LENGTH_SHORT).show()
            }else {
                viewModel.insertArticle(article)
                Snackbar.make(view, "Added Successfully", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}