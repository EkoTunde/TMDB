package com.ekosoftware.tmdb.presentation

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
        private const val DEFAULT_QUERY_TYPE_KEY = ""
        private const val LAST_MOVIE_ID_KEY = "Last movie ID key"
        private val DEFAULT_QUERY_OPTIONS =
            WatchQueryOptions("", WatchQueryOptions.SORT_RATING, WatchQueryOptions.SORT_ORDER_DESC)
        private const val WATCH_LATER_QUERY_OPTIONS_KEY = "watch later query options key"

        const val TYPE_NOW_PLAYING = 1
        const val TYPE_POPULAR = 2
        const val TYPE_TOP_RATED = 3
        const val TYPE_UPCOMING = 4

        private const val SAVED_ERROR_EVENT_KEY = "saved error event key"
    }

    private val currentQueryType: MutableLiveData<Int> = savedStateHandle.getLiveData<Int>(
        DEFAULT_QUERY_TYPE_KEY, TYPE_TOP_RATED
    )

    init {
        setQueryType(TYPE_TOP_RATED)
    }

    fun setQueryType(type: Int) {
        savedStateHandle[DEFAULT_QUERY_TYPE_KEY] = type
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

    private val movieId = savedStateHandle.getLiveData<Long?>(LAST_MOVIE_ID_KEY, null)

    fun setMovieId(id: Long) {
        savedStateHandle[LAST_MOVIE_ID_KEY] = id
    }

    fun clearMovieId() {
        savedStateHandle[LAST_MOVIE_ID_KEY] = null
        movie = null
    }

    private var movie: LiveData<Resource<MovieEntity?>>? = null

    fun getMovie(movieId: Long): LiveData<Resource<MovieEntity?>> = movie
        ?: liveData<Resource<MovieEntity?>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            moviesRepository.getMovie(movieId).collect { emit(it) }
        }.also {
            movie = it
        }

    private val watchWatchQueryOptions: MutableLiveData<WatchQueryOptions> =
        savedStateHandle.getLiveData(WATCH_LATER_QUERY_OPTIONS_KEY, DEFAULT_QUERY_OPTIONS)

    fun setWatchLaterQueryOptions(watchQueryOptions: WatchQueryOptions) {
        savedStateHandle[WATCH_LATER_QUERY_OPTIONS_KEY] = watchQueryOptions
    }

    private var watchLater: LiveData<Resource<List<MovieEntity>>>? = null

    fun getWatchLater(): LiveData<Resource<List<MovieEntity>>> =
        watchLater ?: watchWatchQueryOptions.switchMap {
            liveData<Resource<List<MovieEntity>>> {
                emit(Resource.Loading(null))
                try {
                    emitSource(
                        moviesRepository.getWatchLaterMovies(it.query, it.sortBy, it.sortOrder)
                            .map {
                                Resource.Success(it)
                            }
                    )
                } catch (e: Exception) {
                    emit(Resource.Error(e.message ?: ""))
                }
            }
        }.also {
            watchLater = it
        }

    fun saveToWatchLater() {
        val movieId: Long? = savedStateHandle[LAST_MOVIE_ID_KEY]
        movieId?.let {
            viewModelScope.launch {
                moviesRepository.toggleWatchLater(it)
            }
        }
    }

    fun submitError(errorEvent: ErrorEvent) {
        this.errorEvent.value = errorEvent
    }

    fun errorReceived() {
        savedStateHandle[SAVED_ERROR_EVENT_KEY] = null
    }

    val errorEvent: MutableLiveData<ErrorEvent?> =
        savedStateHandle.getLiveData<ErrorEvent?>(SAVED_ERROR_EVENT_KEY, null)
}

