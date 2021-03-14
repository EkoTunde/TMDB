package com.ekosoftware.tmdb.data.remote

import androidx.paging.PagingSource
import com.ekosoftware.tmdb.data.model.Movie
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TMDB_STARTING_PAGE_INDEX = 1

@Singleton
class SearchMoviesPagingSource @Inject constructor(
    private val query: String,
    private val networkDataSource: NetworkDataSource
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val position = params.key ?: TMDB_STARTING_PAGE_INDEX
        return try {
            val response = networkDataSource.searchMovies(query, position)
            val movies = response.results
            LoadResult.Page(
                data = movies,
                prevKey = if (position == TMDB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (movies.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}