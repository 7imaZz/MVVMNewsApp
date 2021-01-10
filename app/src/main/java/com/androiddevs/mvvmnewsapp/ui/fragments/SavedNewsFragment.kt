package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.utils.OnItemClick
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news), OnItemClick{

    lateinit var viewModel: NewsViewModel
    lateinit var adapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()

        viewModel.getAllArticles().observe(viewLifecycleOwner, {
            adapter.differ.submitList(it)
        })

        val itemTouchHelper = object :
            ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
            ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val article = adapter.differ.currentList[pos]
                viewModel.deleteArticle(article)
                Snackbar.make(view, "Deleted Successfully", Snackbar.LENGTH_LONG).setAction("Undo") {
                    viewModel.insertArticle(article)
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rvSavedNews)
    }

    private fun setupRecyclerView(){
        adapter = NewsAdapter(this)
        rvSavedNews.adapter = adapter
        rvSavedNews.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }
    override fun onItemClickListener(pos: Int) {
        val bundle = Bundle()
        bundle.putSerializable("article", adapter.differ.currentList[pos])
        findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment, bundle)
    }
}