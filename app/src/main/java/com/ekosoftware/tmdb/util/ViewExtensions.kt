package com.ekosoftware.tmdb.util

import android.content.Context
import android.view.View


fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

