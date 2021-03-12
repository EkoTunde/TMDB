package com.ekosoftware.tmdb.util

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

fun ViewBinding.snack(
    msg: String,
    dur: Int = Snackbar.LENGTH_LONG
) = (this.root as ViewGroup).snack(msg,dur)

fun ViewGroup.snack(
    msg: String,
    dur: Int = Snackbar.LENGTH_LONG
) = Snackbar.make(this, msg, dur).show()

fun ViewBinding.snackNBite(
    msg: String,
    dur: Int = Snackbar.LENGTH_LONG,
    actionMsg: String? = null,
    action: (() -> Unit)? = null
) = (this.root as ViewGroup).snackNBite(msg, dur, actionMsg, action)

fun ViewGroup.snackNBite(
    msg: String,
    dur: Int = Snackbar.LENGTH_LONG,
    actionMsg: String? = null,
    action: (() -> Unit)? = null
) {
    val snack = Snackbar.make(this, msg, dur)
    action?.let { _ -> snack.setAction(actionMsg) { _ -> action() } }
    snack.show()
}

fun ViewBinding.snackAndBiteForever(
    msg: String,
    dur: Int = Snackbar.LENGTH_LONG,
    actionMsg: String? = null,
    action: ((v: View) -> Unit)? = null
) = (this.root as ViewGroup).snackAndBiteForever(msg, dur, actionMsg, action)

fun ViewGroup.snackAndBiteForever(
    msg: String,
    dur: Int = Snackbar.LENGTH_LONG,
    actionMsg: String? = null,
    action: ((v: View) -> Unit)? = null
) {
    val snack = Snackbar.make(this, msg, dur)
    action?.let { _ -> snack.setAction(actionMsg) { v -> action(v) } }
    snack.show()
}