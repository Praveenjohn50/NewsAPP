package com.example.newsreader.ui.fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsreader.R
import com.example.newsreader.adapters.NewsAdapter
import com.example.newsreader.database.NewsRepository
import com.example.newsreader.models.Article
import com.example.newsreader.viewmodels.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragmnt_favourites.*

@AndroidEntryPoint
class FavouriteNewsFragment : Fragment(R.layout.fragmnt_favourites) {

    private val viewModel: NewsViewModel by viewModels()
    lateinit var newsAdapter: NewsAdapter
    var favarticles: List<Article>? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Favourite News Fragment")
        context?.let { viewModel.getFavouriteNews(it) }
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)

            }

            findNavController().navigate(
                R.id.action_favouriteTo_article_fragment,
                bundle
            )

        }

        newsAdapter.setOnFavClickListener {

            if (newsAdapter.differ.currentList.size > 0) {

                val currentItem = newsAdapter.differ.currentList[it]

                if (currentItem.isfavourite) {
                    currentItem.isfavourite = false
                    currentItem.let { it1 -> context?.let { it2 -> viewModel.deleteArticle(it2,it1) } }

                } else {
                    currentItem.isfavourite = true
                }

                newsAdapter.differ.currentList[it].isfavourite = currentItem.isfavourite
                newsAdapter.notifyDataSetChanged()
            }
        }


        NewsRepository.favouriteNews.observe(viewLifecycleOwner, { articles ->
            favarticles = articles
            Log.d(TAG, "favcheck: "+articles)
            if(articles.isEmpty()) {
                Log.d(TAG, "favcheck:  show dialog")
                showAlertDialog(true)
            } else {
                Log.d(TAG, "favcheck:  show list")
                newsAdapter.differ.submitList(articles)
                newsAdapter.notifyDataSetChanged()
            }

        })
    }

    private fun setupRecyclerView() {

        newsAdapter = NewsAdapter()

        recyclerview_fav_news.apply {

            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

        }
    }

    private fun showAlertDialog(show : Boolean){
      /*  val alertDialog :AlertDialog.Builder = AlertDialog.Builder(context)

        alertDialog.setTitle("No Favourites added")

        alertDialog.setPositiveButton("OK"){_,_ ->}

        val  alert : AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)

        alert.show()*/

    }

}