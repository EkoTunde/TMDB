package com.ekosoftware.tmdb.util

import com.ekosoftware.tmdb.core.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException

@ExperimentalCoroutinesApi
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: () -> Boolean = { true }
) = channelFlow {
    if (shouldFetch()) {
        val loading = launch {
            query().collect { send(Resource.Loading(it)) }
        }
        try {
            saveFetchResult(fetch())
            loading.cancel()
            query().collect { send(Resource.Success(it)) }
        } catch (e: Exception) {
            loading.cancel()
            query().collect { r -> send(Resource.Error(e.toString(), r)) }
        }
    } else query().collect {
        send(Resource.Success(it))
    }

}