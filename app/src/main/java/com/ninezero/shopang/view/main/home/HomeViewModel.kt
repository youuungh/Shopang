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
    val cartProductsLiveData: LiveData<ResponseWrapper<Any>> =_cartProductsLiveData

    private var isDataLoaded = false

    fun loadData() {
        if (!isDataLoaded) {
            getProducts()
            getCategories()
            isDataLoaded = true
        }
    }

    private fun getProducts() {
        _productsLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _productsLiveData.postValue(homeRepository.getProducts())
        }
    }

    private fun getCategories() {
        _categoriesLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _categoriesLiveData.postValue(homeRepository.getCategories())
        }
    }

    fun wishProductLiveData(id: String): LiveData<Product?> =
        homeRepository.getProductFromWishLiveData(id)

    fun toggleProductInWishlist(product: Product) {
        viewModelScope.launch(IO) {
            homeRepository.toggleProductInWish(product)
        }
    }

    fun addProductToCart(product: Product) {
        viewModelScope.launch(IO) {
            _cartProductsLiveData.postValue(
                homeRepository.addProductsToCart(listOf(product), deleteWishlistProducts = false)
            )
        }
    }

    fun setCartProductsIdle() {
        _cartProductsLiveData.value = ResponseWrapper.Idle()
    }

    override fun onCleared() {
        super.onCleared()
        isDataLoaded = false
    }
}