package com.example.newsreader.database

import android.content.ContentValues.TAG
import android.content.Context
import androidx.room.*

import com.example.newsreader.models.Article

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object AppModule {

    @Singleton
    @Provides
    fun createDatabase(@ApplicationContext app: Context) = Room
        .databaseBuilder(app, ArticleDatabase::class.java, "newsdatabase")
        .build()

    @Singleton
    @Provides
    fun getDao(database: ArticleDatabase) = database.getArticleDao()
}


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): List<Article>

    @Query("DELETE FROM articles WHERE title = :currentTitle")
    suspend fun deleteArticle(currentTitle: String)

    @Query("SELECT url FROM articles")
    fun getFavUrl(): List<String>


}


@Database(
    entities = [Article::class],
    version = 1,
    exportSchema = false
)



abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao


}





