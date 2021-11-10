package com.example.newsreader.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.newsreader.R

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_article_holder.*

@AndroidEntryPoint
class ArticleFragmentHolderActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_holder)

        Log.d(TAG, "onCreate: ArticleFragmentHolderActivity")

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        articleContainer.findNavController().setGraph(R.navigation.article_activity_navi_graph, intent.extras)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}