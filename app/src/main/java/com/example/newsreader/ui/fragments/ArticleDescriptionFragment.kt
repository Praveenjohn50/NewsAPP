package com.example.newsreader.ui.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs

import com.bumptech.glide.Glide

import com.example.newsreader.R
import com.example.newsreader.database.NewsRepository
import com.example.newsreader.models.Article
import com.example.newsreader.viewmodels.NewsViewModel

import com.google.android.material.snackbar.Snackbar

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.android.synthetic.main.fragment_article.*

@AndroidEntryPoint
class ArticleDescriptionFragment : Fragment(R.layout.fragment_article) {

    private val viewModel: NewsViewModel by viewModels()
    private val args: ArticleDescriptionFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val article = args.article
        setupView(article)

        val sharedPreference =  requireContext().getSharedPreferences("CURRENT_STATE", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        editor.putBoolean("state",article.isfavourite)
        editor.commit()

        favourite_button.setOnClickListener {

            if (article.isfavourite) {

                article.let { it1 -> context?.let { it2 -> viewModel.deleteArticle(it2,it1) } }
                Log.d(TAG, "article deleted: " + article.title)
                article.isfavourite = false
                setFavouriteIcon(article)
                Snackbar.make(view, "Favourite Removed", Snackbar.LENGTH_SHORT).show()

            } else {

                context?.let { it1 -> viewModel.addFavourite(it1,article) }
                Log.d(TAG, "article added: " + article.title)
                article.isfavourite = true
                setFavouriteIcon(article)
                Snackbar.make(view, "Marked Favourite", Snackbar.LENGTH_SHORT).show()

            }
            editor.putBoolean("state",article.isfavourite)
            editor.commit()

        }

        share_button.setOnClickListener {

            val newsdata = article.url;
            sendMessage(newsdata.toString())

        }

    }

    fun sendMessage(message: String) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"

        intent.putExtra(Intent.EXTRA_TEXT, message).apply {

            startActivity(intent)

        }
    }

    private fun setupView(article: Article) {
        Glide.with(this).load(article.urlToImage).into(ArticleImage)

        ArticleTitle.text = article.title
        ArticleDescription.text = article.description
        Articlelink.text = article.url

        setFavouriteIcon(article)
    }


    private fun setFavouriteIcon(article: Article) {

        if (article.isfavourite) {

            favourite_button.setImageResource(R.drawable.star_gold)

        } else {

            favourite_button.setImageResource(R.drawable.star_blank)

        }
    }
}