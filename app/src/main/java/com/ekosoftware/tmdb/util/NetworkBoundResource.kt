package com.ekosoftware.tmdb.util

import com.ekosoftware.tmdb.core.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.Exception

/*// T: Type for the Resource data.
// K: Type for the API response.
abstract class NetworkBoundResource<T, K> {
   // Called to save the result of the API response into the database
   @WorkerThread
   protected abstract suspend fun saveCallResult(item: K)

   // Called with the data in the database to decide whether to fetch
   // potentially updated data from the network.
   @MainThread
   protected abstract fun shouldFetch(data: T?): Boolean

   // Called to get the cached data from the database.
   @MainThread
   protected abstract suspend fun loadFromDb(): LiveData<T>

   // Called to create the API call.
   @MainThread
   protected abstract fun createCall(): LiveData<K>

   // Called when the fetch fails. The child class may want to reset components
   // like rate limiter.
   protected open fun onFetchFailed() {}
}*/

@ExperimentalCoroutinesApi
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: () -> Boolean = { true },
    //crossinline onFetchSuccess: () -> Unit = { },
    //crossinline onFetchFailed: (e: Exception) -> Unit = { }
) = channelFlow {
    if (shouldFetch()) {
        val loading = launch {
            query().collect { offer(Resource.Loading(it)) }
        }

        try {
            saveFetchResult(fetch())
            //onFetchSuccess()
            loading.cancel()
            query().collect { send(Resource.Success(it)) }
        } catch (e: Exception) {
            //onFetchFailed(e)
            loading.cancel()
            query().collect { r -> send(Resource.Error(e.toString(), r)) }
        }
    } else {
        query().collect { send(Resource.Success(it)) }
    }
}