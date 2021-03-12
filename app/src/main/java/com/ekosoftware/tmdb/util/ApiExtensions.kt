package com.ekosoftware.tmdb.util

import com.ekosoftware.tmdb.app.Constants

fun String?.asUrl(): String = if (this != null) Constants.BUCKET_URL + this else ""