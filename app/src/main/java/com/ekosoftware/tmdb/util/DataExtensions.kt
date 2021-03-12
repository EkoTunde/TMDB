package com.ekosoftware.tmdb.util

import com.ekosoftware.tmdb.data.model.Genre
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.MovieEntity
import java.lang.StringBuilder

fun Movie.toEntity() = MovieEntity(
    id = this.id,
    name = this.name,
    backdropPath = this.backdropPath,
    budget = this.budget,
    genres = this.genres?.stringify(),
    originalLanguage = this.originalLanguage,
    overview = this.overview,
    popularity = this.popularity,
    posterPath = this.posterPath,
    releaseDate = this.releaseDate,
    revenue = this.revenue,
    status = this.status,
    title = this.title,
    voteAverage = this.voteAverage,
    voteCount = this.voteCount
)

fun List<Genre>.stringify(): String {
    val builder = StringBuilder()
    this.forEachIndexed { i, genre ->
        if (i != 0) builder.append("• ")
        builder.append(genre.name)
    }
    return builder.toString()
}