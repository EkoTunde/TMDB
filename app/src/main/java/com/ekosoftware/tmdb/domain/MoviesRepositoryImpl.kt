package com.ekosoftware.tmdb.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.data.local.LocalDataSource
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.MovieEntity
import com.ekosoftware.tmdb.data.remote.MoviesPagingSource
import com.ekosoftware.tmdb.data.remote.NetworkDataSource
import com.ekosoftware.tmdb.data.remote.TMDBApi
import com.ekosoftware.tmdb.util.networkBoundResource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class MoviesRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val api: TMDBApi,
    private val localDataSource: LocalDataSource
) : MoviesRepository {

    override fun getMoviesForType(typePath: String): LiveData<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviesPagingSource(typePath, api) }
        ).liveData

    @ExperimentalCoroutinesApi
    override suspend fun getMovie(
        movieId: String,
        //onFetchSuccess: () -> Unit,
        //onFetchFailed: (String) -> Unit
    ): Flow<Resource<MovieEntity>> =
        networkBoundResource<MovieEntity, Movie>(
            query = { localDataSource.getMovie(movieId) },
            fetch = { networkDataSource.getMovie(movieId) },
            saveFetchResult = { movie -> localDataSource.save(movie) },
            shouldFetch = { localDataSource.hasMovie(movieId) > 0L },
            //onFetchSuccess = onFetchSuccess,
            //onFetchFailed = { onFetchFailed(it.toString()) }
        )

    override fun getWatchLaterMovies(
        query: String,
        sortBy: String,
        sortOrder: String
    ): LiveData<List<MovieEntity>> = localDataSource.getWatchLaterMovies(query, sortBy, sortOrder)
}