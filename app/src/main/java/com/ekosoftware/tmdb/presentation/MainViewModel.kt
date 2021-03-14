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
    }

    private var movie: LiveData<Resource<MovieEntity?>>? = null

    fun getMovie(): LiveData<Resource<MovieEntity?>> =
        movie ?: movieId.switchMap { id ->
            liveData<Resource<MovieEntity?>>(viewModelScope.coroutineContext + Dispatchers.IO) {
                //emit(Resource.Loading(null))
                //delay(5000)
                /*if (id == null) emit(Resource.Success(null))
                else*/
                id?.let { moviesRepository.getMovie(id).collect { emit(it) } }
            }
        }.also {
            movie = it
        }

    private val watchWatchQueryOptions: MutableLiveData<WatchQueryOptions> =
        savedStateHandle.getLiveData(WATCH_LATER_QUERY_OPTIONS_KEY, DEFAULT_QUERY_OPTIONS)

    fun setWatchLaterQueryOptions(watchQueryOptions: WatchQueryOptions) {
        savedStateHandle[WATCH_LATER_QUERY_OPTIONS_KEY] = watchQueryOptions
    }

    private var watchLater: LiveData<List<Movie>>? = null

    fun getWatchLater(): LiveData<List<Movie>> = watchLater ?: liveData {
        emit(emptyList<Movie>())
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

    private var _incomingError: LiveData<ErrorEvent?>? = null

    val incomingError: LiveData<ErrorEvent?>
        get() = _incomingError ?: errorEvent.switchMap {
            liveData(viewModelScope.coroutineContext + Dispatchers.Default) {
                emit(it)
            }
        }.also {
            _incomingError = it
        }
}

