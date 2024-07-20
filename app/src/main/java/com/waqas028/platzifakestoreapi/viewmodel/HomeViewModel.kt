package com.waqas028.platzifakestoreapi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.waqas028.platzifakestoreapi.network.APIInterface
import com.waqas028.platzifakestoreapi.repository.GenericPagingRepository
import com.waqas028.platzifakestoreapi.response.Product
import com.waqas028.platzifakestoreapi.response.RequestData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val genericPagingRepository: GenericPagingRepository,
    private val apiInterface: APIInterface
) : ViewModel() {
    var getProducts by mutableStateOf<Flow<PagingData<Product>>>(flowOf(PagingData.empty()))

    fun getProducts() = viewModelScope.launch {
        getProducts = genericPagingRepository.getPagingData(
            requestData = RequestData(offset = 1, limit = 10),
            updateRequest = { request, page -> request.copy(offset = page) },
            fetchData = { request -> apiInterface.getProducts(request.offset, request.limit) }
        )
    }
}