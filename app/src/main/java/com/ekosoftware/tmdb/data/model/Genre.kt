package com.ekosoftware.tmdb.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Genre(

    @ColumnInfo(name = "genre_id")
    val id: Long,

    @ColumnInfo(name = "genre_name")
    val name: String = ""

) : Parcelable
