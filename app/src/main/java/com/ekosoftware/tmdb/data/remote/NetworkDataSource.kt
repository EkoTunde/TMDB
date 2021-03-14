package com.ekosoftware.tmdb.data.remote

import com.ekosoftware.tmdb.app.Constants
import com.ekosoftware.tmdb.app.Constants.INCLUDE_ADULT
import com.ekosoftware.tmdb.app.Constants.LANGUAGE
import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.data.model.Genre
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.TMDBResponse
import com.ekosoftware.tmdb.secrets.Secrets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor(private val tmdbApi: TMDBApi) {

    suspend fun getMoviesForType(typePath: String, page: Int) = tmdbApi.getMoviesForType(typePath, page = page)

    suspend fun getMovie(movieId: Long): Movie =
        tmdbApi.getMovie(movieId, Secrets.TMDB_API_KEY, LANGUAGE)

    suspend fun discoverMovies(position: Int): TMDBResponse =
        tmdbApi.discoverMovies(page = position)

    suspend fun searchMovies(query: String, position: Int): TMDBResponse =
        tmdbApi.searchMovies(Secrets.TMDB_API_KEY, LANGUAGE, query, position, INCLUDE_ADULT)

}