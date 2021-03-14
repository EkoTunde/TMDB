package com.ekosoftware.tmdb.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Query
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.MovieEntity
import com.ekosoftware.tmdb.util.toEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(private val movieDao: MovieDao) {

    fun getMovie(movieId: Long) = movieDao.getMovie(movieId)

    fun hasMovie(movieId: Long): Int = movieDao.hasMovie(movieId)

    fun getWatchLaterMovies(): LiveData<List<MovieEntity>> =
        movieDao.getWatchLaterMovies()

    suspend fun save(movie: Movie) = movieDao.insert(movie.toEntity())

    suspend fun toggleWatchLater(movieId: Long) = movieDao.toggleWatchLater(movieId)
}