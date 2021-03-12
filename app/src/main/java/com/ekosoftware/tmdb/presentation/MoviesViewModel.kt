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
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
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

    val lastCheckedType get() = currentQueryType.value ?: TYPE_TOP_RATED

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

    private val movieId = savedStateHandle.getLiveData<String>(LAST_MOVIE_ID_KEY)

    private var movie: LiveData<Resource<MovieEntity>>? = null

    fun getMovie(): LiveData<Resource<MovieEntity>> = movie ?: movieId.switchMap { id ->
        liveData<Resource<MovieEntity>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            moviesRepository.getMovie(id/*, onFetchSuccess, onFetchFailed*/)
                .asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
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
}

data class WatchQueryOptions(
    val query: String,
    val sortBy: String,
    val sortOrder: String
) {
    companion object {
        const val SORT_TITLE = "title"
        const val SORT_RELEASE_DATE = "release_date"
        const val SORT_RATING = "rating"
        const val SORT_ORDER_ASC = "ASC"
        const val SORT_ORDER_DESC = "DESC"
    }
}