package com.ekosoftware.tmdb.data.local

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.MovieEntity
import com.ekosoftware.tmdb.util.toEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(private val movieDao: MovieDao) {

    fun getMovie(movieId: String) = movieDao.getMovie(movieId)

    fun hasMovie(movieId: String): Long = movieDao.hasMovie(movieId)

    fun getWatchLaterMovies(
        query: String,
        sortBy: String,
        sortOrder: String
    ): LiveData<List<MovieEntity>> = if (sortOrder == "ASC") {
        movieDao.getWatchLaterMoviesAsc(query, sortBy)
    } else {
        movieDao.getWatchLaterMoviesDesc(query, sortBy)
    }

    suspend fun save(movie: Movie) = movieDao.insert(movie.toEntity())
}