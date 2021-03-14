package com.ekosoftware.tmdb.presentation

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.MovieEntity
import com.ekosoftware.tmdb.domain.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
        private const val MOVIES_OPTIONS_KEY = "movies options key"

        private const val DEFAULT_WATCH_LATER_QUERY = ""
        private const val WATCH_LATER_QUERY_KEY = "watch later query key"

        private const val SEARCH_QUERY_KEY = "search query key"

        const val TYPE_NOW_PLAYING = 1
        const val TYPE_POPULAR = 2
        const val TYPE_TOP_RATED = 3
        const val TYPE_UPCOMING = 4

        private const val SAVED_ERROR_EVENT_KEY = "saved error event key"
    }

    private val currentQueryType: MutableLiveData<Int> =
        savedStateHandle.getLiveData<Int>(MOVIES_OPTIONS_KEY, TYPE_TOP_RATED)

    init {
        setQueryType(TYPE_TOP_RATED)
    }

    fun setQueryType(type: Int) {
        savedStateHandle[MOVIES_OPTIONS_KEY] = type
    }

    private var _movies: LiveData<PagingData<Movie>>? = null

    val movies
        get() = _movies ?: currentQueryType.switchMap { type ->
            when (type) {
                TYPE_NOW_PLAYING -> moviesRepository.getMoviesForType("now_playing")
                    .cachedIn(viewModelScope)
                TYPE_POPULAR -> moviesRepository.getMoviesForType("popular")
                    .cachedIn(viewModelScope)
                TYPE_TOP_RATED -> moviesRepository.getMoviesForType("top_rated")
                    .cachedIn(viewModelScope)
                TYPE_UPCOMING -> moviesRepository.getMoviesForType("upcoming")
                    .cachedIn(viewModelScope)
                else -> throw IllegalArgumentException("Given argument type $type isn't valid.")
            }
        }.also {
            _movies = it
        }

    fun clearMovie() {
        movie = null
    }

    private var movie: LiveData<Resource<MovieEntity?>>? = null

    fun getMovie(movieId: Long): LiveData<Resource<MovieEntity?>> = movie
        ?: liveData<Resource<MovieEntity?>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            moviesRepository.getMovie(movieId).collect {
                Log.d(TAG, "getMovie: $it")
                emit(it)
            }
        }.also {
            movie = it
        }

    private val searchQuery = savedStateHandle.getLiveData<String>(SEARCH_QUERY_KEY, null)

    fun submitMovieQuery(query: String?) {
        savedStateHandle[SEARCH_QUERY_KEY] = query
    }

    private var _searchedMovies: LiveData<PagingData<Movie>>? = null

    val searchedMovies: LiveData<PagingData<Movie>>
        get() = _searchedMovies ?: searchQuery.switchMap { query ->
            if (query.isNullOrEmpty()) {
                moviesRepository.discoverMovies().cachedIn(viewModelScope)
            } else {
                moviesRepository.searchMovies(query).cachedIn(viewModelScope)
            }
        }.also {
            _searchedMovies = it
        }

    private var watchLater: LiveData<Resource<List<MovieEntity>>>? = null

    fun getWatchLater(): LiveData<Resource<List<MovieEntity>>> =
        watchLater ?: liveData<Resource<List<MovieEntity>>> {
            emit(Resource.Loading(null))
            try {
                emitSource(
                    moviesRepository.getWatchLaterMovies().map { Resource.Success(it) }
                )
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: ""))
            }
        }.also {
            watchLater = it
        }

    fun saveToWatchLater(movieId: Long) = viewModelScope.launch {
        moviesRepository.toggleWatchLater(movieId)
    }
}