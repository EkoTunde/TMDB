package com.ekosoftware.tmdb.data.model

import android.os.Parcelable
import com.ekosoftware.tmdb.app.Constants.BUCKET_URL
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(

    @SerializedName("backdrop_path")
    val backdropPath: String? = null,

    @SerializedName("budget")
    val budget: Long = 0,

    @SerializedName("genres")
    val genres: List<Genre>? = null,

    @SerializedName("genre_ids")
    val genresIds: List<Int>? = null,

    @SerializedName("id")
    val id: Long = 0L,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("original_language")
    val originalLanguage: String = "",

    @SerializedName("overview")
    val overview: String = "",

    @SerializedName("popularity")
    val popularity: Double = 0.0,

    @SerializedName("poster_path")
    val posterPath: String? = null,

    @SerializedName("release_date")
    val releaseDate: String = "",

    @SerializedName("revenue")
    val revenue: Long? = null,

    @SerializedName("status")
    val status: String = "",

    @SerializedName("title")
    val title: String = "",

    @SerializedName("vote_average")
    val voteAverage: Double = 0.0,

    @SerializedName("vote_count")
    val voteCount: Long = 0L

) : Parcelable 