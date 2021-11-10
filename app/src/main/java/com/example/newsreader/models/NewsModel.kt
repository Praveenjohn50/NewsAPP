package com.example.newsreader.models

import androidx.room.*

import java.io.Serializable

@Entity(
    tableName = "articles", indices = [(Index(value = ["url"], unique = true))]
)
data class Article(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val title: String?,
    val url: String?,

    var isfavourite: Boolean,

    val urlToImage: String?

) : Serializable


data class NewsResponse(

    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int

)


