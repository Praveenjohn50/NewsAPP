package com.example.newsreader.ui

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController

import com.example.newsreader.R

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainNewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: ")

        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener {
        }
    }
}