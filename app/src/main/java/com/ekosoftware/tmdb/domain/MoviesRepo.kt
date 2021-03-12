package com.ekosoftware.tmdb.domain

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.remote.MoviesPagingSource
import com.ekosoftware.tmdb.data.remote.TMDBApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepo @Inject constructor(private val api: TMDBApi) {
   /* fun getMoviesResult(): LiveData<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviesPagingSource(api) }
        ).liveData*/
}