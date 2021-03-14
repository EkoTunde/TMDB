package com.ekosoftware.tmdb.presentation

data class ErrorEvent(
    val msg: String,
    val actionMsg: String? = null,
    val action: (() -> Unit)? = null
)