package com.ekosoftware.tmdb.domain

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.MovieEntity
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    fun getMoviesForType(typePath: String): LiveData<PagingData<Movie>>

    suspend fun getMovie(movieId: String): Flow<Resource<MovieEntity>>

    fun getWatchLaterMovies(
        query: String,
        sortBy: String,
        sortOrder: String
    ): LiveData<List<MovieEntity>>
}