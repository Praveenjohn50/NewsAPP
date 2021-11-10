package com.example.newsreader.database

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsreader.models.Article
import com.example.newsreader.models.NewsResponse
import com.example.newsreader.retroapi.RetrofitInstance
import com.example.newsreader.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Response

object NewsRepository {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val favouriteNews: MutableLiveData<List<Article>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null
    var oldArticles1: List<Article>? = null
    var favUrlList : List<String>? = null
    var dababaseOperations : DatabaseOperations? = null



    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    fun initializeDB(context: Context) : DatabaseOperations {
        return DatabaseOperations.getDatabaseClient(context)
    }


    fun addFavourite(context: Context, favarticle: Article) {
        dababaseOperations = initializeDB(context)
        oldArticles1?.find { article -> article.url == favarticle.url }?.isfavourite = true
        CoroutineScope(IO).launch {
            dababaseOperations!!.articlDao().insertArticle(favarticle)
        }


    }


    fun removeFavourite(context: Context, removearticle: Article) {
        dababaseOperations = initializeDB(context)
        oldArticles1?.find { article -> article.url == removearticle.url }?.isfavourite = false
        CoroutineScope(IO).launch {
            removearticle.title?.let { dababaseOperations!!.articlDao().deleteArticle(it) }
        }
    }

    fun getFavoutiteNews(context: Context) {
        dababaseOperations = initializeDB(context)

        CoroutineScope(IO).launch {
            favUrlList = dababaseOperations!!.articlDao().getFavUrl()
            favouriteNews.postValue(dababaseOperations!!.articlDao().getAllArticles())
        }
    }


    suspend fun getBreakingNews(countryCode: String): Resource<NewsResponse>? {
        breakingNews.postValue(Resource.Loading())
        val response = getBreakingNews(countryCode, breakingNewsPage)
        var handleData = handleBreakingNewsPostValue(response)
        breakingNews.postValue(handleData)
        return handleData
    }


    fun handleBreakingNewsPostValue(response: Response<NewsResponse>): Resource<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let {

                breakingNewsPage++

                if (breakingNewsResponse == null) {
                    breakingNewsResponse = it
                    oldArticles1 = it.articles
                    if (oldArticles1 != null && favUrlList!= null) {
                        for (i in 0 until (oldArticles1 as MutableList<Article>).size) {
                            if (favUrlList!!.contains((oldArticles1 as MutableList<Article>)[i].url.toString())) {
                                (oldArticles1 as MutableList<Article>)[i].isfavourite = true
                            }
                        }
                    }

                } else {

                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = it.articles
                    oldArticles?.addAll(newArticles)
                    if (oldArticles != null && favUrlList!= null) {
                        for (i in 0 until oldArticles.size) {
                            if (favUrlList!!.contains(oldArticles[i].url.toString())) {
                                oldArticles[i].isfavourite = true
                            }
                        }
                    }
                    oldArticles1 = oldArticles

                }

                return Resource.Success(breakingNewsResponse ?: it)
            }

        }
        return Resource.Error(response.message())
    }


}



@Database(entities = arrayOf(Article::class), version = 1, exportSchema = false)
abstract class DatabaseOperations : RoomDatabase() {

    abstract fun articlDao() : ArticleDao

    companion object {

        @Volatile
        private var INSTANCE: DatabaseOperations? = null

        fun getDatabaseClient(context: Context) : DatabaseOperations {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, DatabaseOperations::class.java, "LOGIN_DATABASE")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}


