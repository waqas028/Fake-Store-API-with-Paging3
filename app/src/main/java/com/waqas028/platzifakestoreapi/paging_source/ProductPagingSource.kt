package com.waqas028.platzifakestoreapi.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.waqas028.platzifakestoreapi.network.APIInterface
import com.waqas028.platzifakestoreapi.response.Product


class ProductPagingSource(private val apiInterface: APIInterface ) : PagingSource<Int, Product>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 0
        return try {
            val response = apiInterface.getProducts(page, 10)
            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

