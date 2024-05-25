package com.ninezero.shopang.view.main.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninezero.shopang.data.repository.HomeRepository
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    private val _searchProductsLiveData = MutableLiveData<ResponseWrapper<List<Product>>>()
    val searchProductsLiveData: LiveData<ResponseWrapper<List<Product>>> = _searchProductsLiveData

    private var searchJob: Job? = null

    fun searchProducts(query: String) {
        searchJob?.cancel()
        if (query.isNotBlank()) {
            searchJob = viewModelScope.launch(IO) {
                delay(500)
                _searchProductsLiveData.postValue(ResponseWrapper.Loading())
                val response = homeRepository.searchProducts(query)
                _searchProductsLiveData.postValue(response)
            }
        } else {
            _searchProductsLiveData.value = ResponseWrapper.Idle()
        }
    }
}