package com.ekosoftware.tmdb.data.remote

import com.ekosoftware.tmdb.app.Constants
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.TMDBResponse
import com.ekosoftware.tmdb.secrets.Secrets
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApi {

    @GET("movie/{type}?")
    suspend fun getMoviesForType(
        @Path("type") type: String = Constants.DEFAULT_TYPE,
        @Query("api_key") apiKey: String = Secrets.TMDB_API_KEY,
        @Query("page") page: Int = Constants.FIRST_PAGE,
    ): TMDBResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String = Secrets.TMDB_API_KEY,
        @Query("language") language: String = Constants.LANGUAGE,
        @Query("sort_by") sortBy: String = Constants.SORT_BY,
        @Query("include_adult") include_adult: Boolean = Constants.INCLUDE_ADULT,
        @Query("include_video") includeVideo: Boolean = Constants.INCLUDE_VIDEO,
        @Query("page") page: Int = Constants.FIRST_PAGE,
        @Query("vote_count.gte") voteCount: Int = Constants.VOTE_MIN_COUNT,
        @Query("vote_average.gte") voteAverage: Int = Constants.VOTE_MIN_AVERAGE,
    ): TMDBResponse

    @GET("search/movie?")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = Secrets.TMDB_API_KEY,
        @Query("language") language: String = Constants.LANGUAGE,
        @Query("query") query: String,
        @Query("page") page: Int = Constants.FIRST_PAGE,
        @Query("include_adult") include_adult: Boolean = Constants.INCLUDE_ADULT
    ): TMDBResponse

    @GET("movie/{movie_id}?")
    suspend fun getMovie(
        @Path("movie_id") movieId: Long,
        @Query("api_key") apiKey: String = Secrets.TMDB_API_KEY,
        @Query("language") language: String = Constants.LANGUAGE
    ): Movie

}