package com.example.newsreader.retroapi

import com.example.newsreader.models.NewsResponse
import com.example.newsreader.utils.NewsAppConstants

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("/v2/top-headlines")
    suspend fun getBreakingNews(

        @Query("country") countryName: String = "us",
        @Query("page") pageNo: Int = 1,
        @Query("apiKey") apikey: String = NewsAppConstants.API_KEY

    ): Response<NewsResponse>


}

class RetrofitInstance {
    companion object {

        private val retrofit by lazy {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            Retrofit.Builder()
                .baseUrl(NewsAppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        }


        val api by lazy {

            retrofit.create(NewsApi::class.java)

        }

    }

}