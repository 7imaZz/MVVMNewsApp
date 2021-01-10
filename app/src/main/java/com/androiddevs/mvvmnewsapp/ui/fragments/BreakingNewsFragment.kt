package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.utils.OnItemClick
import com.androiddevs.mvvmnewsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news), OnItemClick{

    lateinit var viewModel: NewsViewModel
    lateinit var adapter: NewsAdapter
    lateinit var myScrollListener: RecyclerView.OnScrollListener

    var isLoading = false
    var isScrolling = false
    var isLastPage = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel

        myScrollListener = object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
                val isAtLastItem = firstVisibleItem + visibleItemCount >= totalItemCount
                val isNotAtFirstItem = firstVisibleItem >= 0
                val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

                val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtFirstItem && isTotalMoreThanVisible

                if (shouldPaginate){
                    viewModel.getBreakingNews("eg")
                    isScrolling = false
                }
            }
        }

        setupRecyclerView()

        viewModel.breakingNewsLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success ->{
                    hideProgressBar()
                    adapter.differ.submitList(it.data!!.articles.toList())
                    val totalPages = it.data.totalResults / QUERY_PAGE_SIZE + 2
                    isLastPage = totalPages == viewModel.breakingNewsPage

                    if (isLastPage){
                        rvBreakingNews.setPadding(0, 0, 0, 0)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Log.e("TAG", "onViewCreated: ${it.message}")
                }
                is Resource.Loading -> showProgressBar()
            }
        })

    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setupRecyclerView(){
        adapter = NewsAdapter(this)
        rvBreakingNews.adapter = adapter
        rvBreakingNews.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        rvBreakingNews.addOnScrollListener(myScrollListener)
    }
    override fun onItemClickListener(pos: Int) {
        val bundle = Bundle()
        bundle.putSerializable("article", adapter.differ.currentList[pos])
        findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
    }
}