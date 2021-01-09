package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.utils.OnItemClick
import com.androiddevs.mvvmnewsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news), OnItemClick{

    lateinit var viewModel: NewsViewModel
    lateinit var adapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()

        viewModel.breakingNewsLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success ->{
                    hideProgressBar()
                    adapter.differ.submitList(it.data!!.articles)
                }
                is Resource.Error ->
                    Log.e("TAG", "onViewCreated: ${it.message}")
                is Resource.Loading -> showProgressBar()
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.GONE
    }

    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerView(){
        adapter = NewsAdapter(this)
        rvBreakingNews.adapter = adapter
        rvBreakingNews.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }
    override fun onItemClickListener(pos: Int) {
        Toast.makeText(requireContext(), adapter.differ.currentList[pos].title, Toast.LENGTH_SHORT).show()
    }
}