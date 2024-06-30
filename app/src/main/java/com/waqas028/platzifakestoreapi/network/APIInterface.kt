package com.waqas028.platzifakestoreapi.network

import com.waqas028.platzifakestoreapi.response.Product
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {
    @GET("products/")
    suspend fun getProducts(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): List<Product>
}