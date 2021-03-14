package com.ekosoftware.tmdb.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekosoftware.tmdb.core.BaseDao
import com.ekosoftware.tmdb.data.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao : BaseDao<MovieEntity> {

    @Query("SELECT * FROM movies_table WHERE movie_id = :movieId")
    fun getMovie(movieId: Long): Flow<MovieEntity>

    @Query("SELECT COUNT(movie_id) FROM movies_table WHERE movie_id = :movieId")
    fun hasMovie(movieId: Long): Int

    @Query("SELECT * FROM movies_table WHERE watch_later = 1 AND name LIKE '%' || :query || '%' ORDER BY :sortBy ASC")
    fun getWatchLaterMoviesAsc(
        query: String,
        sortBy: String
    ): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM movies_table WHERE watch_later = 1 AND name LIKE '%' || :query || '%' ORDER BY :sortBy DESC")
    fun getWatchLaterMoviesDesc(
        query: String,
        sortBy: String
    ): LiveData<List<MovieEntity>>

    @Query("UPDATE movies_table SET watch_later = CASE watch_later WHEN 1 THEN 0 ELSE 1 END WHERE movie_id = :movieId ")
    suspend fun toggleWatchLater(movieId: Long)
}