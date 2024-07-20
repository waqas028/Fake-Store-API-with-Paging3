package com.waqas028.platzifakestoreapi.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import java.io.IOException

class GenericPagingDataSource<T : Any, R>(
    private val apiService: suspend (R) -> List<T>,
    private val requestData: R,
    private val updateRequest: (R, Int) -> R
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val nextPage = params.key ?: 1
            val updatedRequestData = updateRequest(requestData, nextPage)
            val responseData = apiService(updatedRequestData)
            LoadResult.Page(
                data = responseData,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (responseData.isNotEmpty()) nextPage + 1 else null
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}

