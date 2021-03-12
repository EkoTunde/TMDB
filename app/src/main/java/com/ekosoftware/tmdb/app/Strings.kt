package com.ekosoftware.tmdb.app

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringRes, *formatArgs)
    }

    fun getQuantity(
        @PluralsRes pluralsRes: Int,
        quantity: Int,
        vararg formatArgs: Any = emptyArray()
    ): String {
        return App.instance.resources.getQuantityString(pluralsRes, quantity, *formatArgs)
    }
}