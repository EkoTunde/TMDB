package com.ekosoftware.tmdb.app

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

object Drawables {
    fun get(@DrawableRes drawableResId: Int): Drawable? {
        return ResourcesCompat.getDrawable(App.instance.resources, drawableResId, App.instance.theme)
    }
}