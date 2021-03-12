package com.ekosoftware.tmdb.data.remote

import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.TMDBResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApi {

    @GET("movie/{type}?")
    suspend fun getMoviesForType(
        @Path("type") type: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): TMDBResponse

    @GET("movie/now_playing?")
    suspend fun getMoviesNowPlaying(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): TMDBResponse

    @GET("movie/popular?")
    suspend fun getMoviesPopular(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): TMDBResponse

    @GET("movie/top_rated?")
    suspend fun getMoviesTopRated(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): TMDBResponse

    @GET("movie/upcoming?")
    suspend fun getMoviesUpcoming(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): TMDBResponse

    @GET("movie/{movie_id}?")
    suspend fun getMovie(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Movie

}
