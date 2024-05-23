package com.ninezero.shopang.view.main.wish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninezero.shopang.data.repository.HomeRepository
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    private lateinit var _wishProductsLiveData: LiveData<List<Product>>
    val wishProductsLiveData
        get() = _wishProductsLiveData

    private val _cartProductsLiveData = MutableLiveData<ResponseWrapper<Any>>()
    val cartProductsLiveData: LiveData<ResponseWrapper<Any>> = _cartProductsLiveData

    fun getAllWishes() {
        viewModelScope.launch(IO) {
            _wishProductsLiveData = homeRepository.getAllWishesLiveData()
        }
    }

    fun deleteWish(product: Product) {
        viewModelScope.launch(IO) {
            homeRepository.toggleProductInWish(product)
        }
    }

    fun addWishToCart(product: Product) {
        _cartProductsLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _cartProductsLiveData.postValue(
                homeRepository.addProductToCartFromWish(product)
            )
            _cartProductsLiveData.postValue(ResponseWrapper.Idle())
        }
    }

    fun addWishesToCart(wishList: List<Product>) {
        _cartProductsLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _cartProductsLiveData.postValue(
                homeRepository.addProductsToCart(wishList, deleteWishlistProducts = true)
            )
            _cartProductsLiveData.postValue(ResponseWrapper.Idle())
        }
    }
}