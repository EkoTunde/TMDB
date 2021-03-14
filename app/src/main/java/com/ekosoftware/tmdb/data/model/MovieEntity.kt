package com.ekosoftware.tmdb.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "movies_table")
data class MovieEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "movie_id")
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "backdrop_path")
    var backdropPath: String? = null,

    @ColumnInfo(name = "budget")
    var budget: Long = 0,

    @ColumnInfo(name = "original_language")
    var originalLanguage: String = "",

    @ColumnInfo(name = "genres")
    var genres: String? = null,

    @ColumnInfo(name = "overview")
    var overview: String = "",

    @ColumnInfo(name = "popularity")
    var popularity: Double = 0.0,

    @ColumnInfo(name = "poster_path")
    var posterPath: String? = null,

    @ColumnInfo(name = "release_date")
    var releaseDate: String = "",

    @ColumnInfo(name = "revenue")
    var revenue: Long? = null,

    @ColumnInfo(name = "runtime")
    var runtime: Int? = null,

    @ColumnInfo(name = "status")
    var status: String = "",

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "rating")
    var rating: Double = 0.0,

    @ColumnInfo(name = "vote_count")
    var voteCount: Long = 0L,

    @ColumnInfo(name = "watch_later")
    var watchLater: Boolean = false

) : Parcelable
