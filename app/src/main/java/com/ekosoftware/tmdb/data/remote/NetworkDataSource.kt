package com.ekosoftware.tmdb.data.remote

import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.data.model.Genre
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.secrets.Secrets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor(private val tmdbApi: TMDBApi) {

    private val generateMovieList = listOf(
        Movie(
            genres = listOf(Genre(28), Genre(35), Genre(10751), Genre(16), Genre(12)),
            id = 587807,
            overview = "Tom the cat and Jerry the mouse get kicked out of their home and relocate to a fancy New York hotel, where a scrappy employee named Kayla will lose her job if she can’t evict Jerry before a high-class wedding at the hotel. Her solution? Hiring Tom to get rid of the pesky mouse.",
            popularity = 5244.617,
            posterPath = "/6KErczPBROQty7QoIsaa6wJYXZi.jpg",
            releaseDate = "2021-02-11",
            title = "Tom & Jerry",
            voteAverage = 7.8,
            voteCount = 676
        ),
        Movie(
            genres = listOf(Genre(14), Genre(28), Genre(12)),
            id = 458576,
            overview = "A portal transports Cpt. Artemis and an elite unit of soldiers to a strange world where powerful monsters rule with deadly ferocity. Faced with relentless danger, the team encounters a mysterious hunter who may be their only hope to find a way home.",
            popularity = 3434.38,
            posterPath = "/1UCOF11QCw8kcqvce8LKOO6pimh.jpg",
            releaseDate = "2020-12-03",
            title = "Monster Hunter",
            voteAverage = 7.3,
            voteCount = 1009
        )
    )

    suspend fun getMovie(movieId: String): Movie =
        tmdbApi.getMovie(movieId, Secrets.TMDB_API_KEY, "en-US")

}