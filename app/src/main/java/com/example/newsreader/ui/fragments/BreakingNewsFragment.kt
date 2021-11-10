package com.example.newsreader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.newsreader.R
import com.example.newsreader.adapters.NewsAdapter
import com.example.newsreader.database.NewsRepository
import com.example.newsreader.models.Article
import com.example.newsreader.utils.NewsAppConstants.NEWS_PAGE_SIZE
import com.example.newsreader.utils.Resource
import com.example.newsreader.viewmodels.NewsViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_breaking_news.*


@AndroidEntryPoint
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private val TAG = "Breaking News Fragment"

    private val newsViewModel: NewsViewModel by viewModels()
    lateinit var newsAdapter: NewsAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var clickIntemPosition: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: Breaking News Fragment")

        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            newsAdapter.setOnItemPositionClickListener {
                clickIntemPosition = it
            }

            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        newsAdapter.setOnFavClickListener {

            if (newsAdapter.differ.currentList.size > 0) {

                val currentItem = newsAdapter.differ.currentList[it]
                if (currentItem.isfavourite) {

                    currentItem.isfavourite = false
                    currentItem.let { it1 -> newsViewModel.deleteArticle(requireContext(),it1) }

                } else {

                    currentItem.isfavourite = true
                    newsViewModel.addFavourite(requireContext(), currentItem)
                }

                newsAdapter.differ.currentList[it].isfavourite = currentItem.isfavourite
                newsAdapter.notifyDataSetChanged()
            }
        }

        apiCall()

        swipeContainer.setOnRefreshListener {
            apiCall()
        }

    }

    private fun apiCall() {
        newsAdapter.differ.submitList(NewsRepository.oldArticles1)
        newsAdapter.notifyDataSetChanged()

        NewsRepository.breakingNews.observe(viewLifecycleOwner, { response ->

            when (response) {

                is Resource.Success -> {


                    hideProgressBar()

                    Log.d(TAG, "onViewCreated: " + response.data)
                    response.data?.let { newsResponse ->
                        swipeContainer.isRefreshing = false
                        val totalpages = newsResponse.totalResults / NEWS_PAGE_SIZE
                        isLastPage = NewsRepository.breakingNewsPage == totalpages

                        if (isLastPage) {

                            recyclerViewBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {

                    hideProgressBar()

                    response.message?.let { message -> Log.e(TAG, message) }
                }
                is Resource.Loading -> {

                    showProgressBar()
                }
            }
        })
    }

    private fun showProgressBar() {

        progressbarRecyclerView.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        progressbarRecyclerView.visibility = View.INVISIBLE

        isLoading = false
    }


    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true

            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= NEWS_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem &&
                    isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                context?.let { newsViewModel.getFavouriteNews(it) }
                newsViewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {

        newsAdapter = NewsAdapter()

        Log.d(TAG, "setupRecyclerView: ")
        recyclerViewBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)

        }
    }

    override fun onResume() {
        super.onResume()
       newsAdapter.notifyDataSetChanged()
    }


}