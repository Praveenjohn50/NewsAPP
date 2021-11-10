package com.example.newsreader.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import com.example.newsreader.R
import com.example.newsreader.models.Article

import kotlinx.android.synthetic.main.item_recycler_article.view.*


class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    private val diffCallBack = object : DiffUtil.ItemCallback<Article>() {

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }
    }


    val differ = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {

        return ArticleViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler_article, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        val article = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            textTitle.text = article.title
            textDescription.text = article.description
            publishedatText.text = article.publishedAt
            textauthor.text = article.author

            if (article.isfavourite) {
                favourite_button.setImageResource(R.drawable.star_gold)
            } else {
                favourite_button.setImageResource(R.drawable.star_blank)
            }

            itemRecyclerview.setOnClickListener {
                onItemClickListener?.let { it(article) }

                onItemPositionClickListener?.let { it(position) }
            }

            favourite_button.setOnClickListener {
                onFavClickListener?.let { it(position) }
            }
        }

    }

    private var onFavClickListener: ((Int) -> Unit)? = null


    fun setOnFavClickListener(listener: (Int) -> Unit) {
        onFavClickListener = listener
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    private var onItemPositionClickListener: ((Int)-> Unit)? = null


    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemPositionClickListener(listener: (Int) -> Unit) {
        onItemPositionClickListener = listener
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}