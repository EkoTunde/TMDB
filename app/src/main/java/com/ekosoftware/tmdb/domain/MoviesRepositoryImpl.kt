package com.ekosoftware.tmdb.domain

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.data.local.LocalDataSource
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.MovieEntity
import com.ekosoftware.tmdb.data.remote.*
import com.ekosoftware.tmdb.util.networkBoundResource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class MoviesRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource
) : MoviesRepository {

    override fun getMoviesForType(typePath: String): LiveData<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviesPagingSource(networkDataSource, typePath) }
        ).liveData

    override fun discoverMovies(): LiveData<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DiscoverMoviesPagingSource(networkDataSource) }
        ).liveData

    override fun searchMovies(query: String): LiveData<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchMoviesPagingSource(query, networkDataSource) }
        ).liveData

    @ExperimentalCoroutinesApi
    override suspend fun getMovie(movieId: Long): Flow<Resource<MovieEntity>> =
        networkBoundResource<MovieEntity, Movie>(
            query = { localDataSource.getMovie(movieId) },
            fetch = { networkDataSource.getMovie(movieId) },
            saveFetchResult = { movie -> localDataSource.save(movie) },
            shouldFetch = { localDataSource.hasMovie(movieId) < 1 }
        )

    override fun getWatchLaterMovies(): LiveData<List<MovieEntity>> =
        localDataSource.getWatchLaterMovies()

    override suspend fun toggleWatchLater(movieId: Long) = localDataSource.toggleWatchLater(movieId)
}