package com.ekosoftware.tmdb.domain

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.MovieEntity
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    fun getMoviesForType(typePath: String): LiveData<PagingData<Movie>>
    fun discoverMovies(): LiveData<PagingData<Movie>>
    fun searchMovies(query: String): LiveData<PagingData<Movie>>
    suspend fun getMovie(movieId: Long): Flow<Resource<MovieEntity>>
    fun getWatchLaterMovies(): LiveData<List<MovieEntity>>
    suspend fun toggleWatchLater(movieId: Long)
}