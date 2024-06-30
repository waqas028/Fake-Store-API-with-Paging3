package com.waqas028.platzifakestoreapi.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.waqas028.platzifakestoreapi.network.APIInterface
import com.waqas028.platzifakestoreapi.paging_source.ProductPagingSource
import com.waqas028.platzifakestoreapi.response.Product
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiInterface: APIInterface,
) {

    fun getProducts(): Pager<Int, Product> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductPagingSource(
                apiInterface
            )
        }
    )
}