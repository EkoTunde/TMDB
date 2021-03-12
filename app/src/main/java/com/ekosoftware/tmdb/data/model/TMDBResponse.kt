package com.ekosoftware.tmdb.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TMDBResponse(
    @SerializedName("results")
    val results: List<Movie>
) : Parcelable