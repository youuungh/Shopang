package com.ninezero.shopang.view.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninezero.shopang.data.repository.HomeRepository
import com.ninezero.shopang.model.Category
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    private val _productsLiveData = MutableLiveData<ResponseWrapper<List<Product>>>()
    val productsLiveData: LiveData<ResponseWrapper<List<Product>>>
        get() = _productsLiveData

    private val _categoriesLiveData = MutableLiveData<ResponseWrapper<List<Category>>>()
    val categoriesLiveData: LiveData<ResponseWrapper<List<Category>>>
        get() = _categoriesLiveData

    private val _cartProductsLiveData = MutableLiveData<ResponseWrapper<Any>>()
    val cartProductsLiveData: LiveData<ResponseWrapper<Any>> = _cartProductsLiveData

    private var isDataLoaded = false

    fun setCartProductValue() {
        _cartProductsLiveData.value = ResponseWrapper.Idle()
    }

    fun getProducts() {
        if (!isDataLoaded) {
            _productsLiveData.value = ResponseWrapper.Loading()
            viewModelScope.launch(IO) {
                _productsLiveData.postValue(homeRepository.getProducts())
                isDataLoaded = true
            }
        }
    }

    fun getCategories() {
        if (!isDataLoaded) {
            _categoriesLiveData.value = ResponseWrapper.Loading()
            viewModelScope.launch(IO) {
                _categoriesLiveData.postValue(homeRepository.getCategories())
                isDataLoaded = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        isDataLoaded = false
    }
}