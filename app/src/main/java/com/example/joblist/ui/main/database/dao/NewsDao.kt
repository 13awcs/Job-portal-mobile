package com.example.joblist.ui.main.database.dao

import androidx.room.*
import com.example.joblist.entities.News

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY id DESC")
    fun getAll(): MutableList<News>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg newsList: News)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(news: News)

    @Update
    suspend fun update(news: News)

    @Delete
    suspend fun delete(news: News)
}
