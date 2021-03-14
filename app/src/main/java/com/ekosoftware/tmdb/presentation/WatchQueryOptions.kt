package com.ekosoftware.tmdb.presentation

data class WatchQueryOptions(
    val query: String,
    val sortBy: String,
    val sortOrder: String
) {
    companion object {
        const val SORT_TITLE = "title"
        const val SORT_RELEASE_DATE = "release_date"
        const val SORT_RATING = "rating"
        const val SORT_ORDER_ASC = "ASC"
        const val SORT_ORDER_DESC = "DESC"
    }
}