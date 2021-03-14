package com.ekosoftware.tmdb.util

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

object SnackBarExtensions {
    const val DEFAULT_DURATION = Snackbar.LENGTH_LONG
}

fun ViewBinding.snack(
    msg: String,
    dur: Int = Snackbar.LENGTH_LONG
) = (this.root as ViewGroup).snack(msg, dur)

fun ViewGroup.snack(
    msg: String,
    dur: Int = Snackbar.LENGTH_LONG
) = Snackbar.make(this, msg, dur).show()

fun ViewBinding.snackNBite(
    msg: String,
    action: Pair<String, () -> Unit>? = null,
    dur: Int = Snackbar.LENGTH_LONG
) = (this.root as ViewGroup).snackNBite(msg, action, dur)

fun ViewGroup.snackNBite(
    msg: String,
    action: Pair<String, () -> Unit>? = null,
    dur: Int = Snackbar.LENGTH_LONG
) = Snackbar.make(this, msg, dur).also {
    action?.let { _ -> it.setAction(action.first) { _ -> action.second.invoke() } }
    it.show()
}

fun ViewBinding.snackAndBiteForever(
    msg: String,
    action: Pair<String, () -> Unit>? = null,
    dur: Int = Snackbar.LENGTH_LONG,
) = (this.root as ViewGroup).snackAndBiteForever(msg, action, dur)

fun ViewGroup.snackAndBiteForever(
    msg: String,
    action: Pair<String, () -> Unit>? = null,
    dur: Int = Snackbar.LENGTH_LONG
) = Snackbar.make(this, msg, dur).also {
    action?.let { _ -> it.setAction(action.first) { action.second.invoke() } }
    it.show()
}