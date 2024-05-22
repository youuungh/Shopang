package com.ninezero.shopang.view.main.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninezero.shopang.data.repository.HomeRepository
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _cartProductsLiveData = MutableLiveData<ResponseWrapper<List<Product>>>()
    val cartProductsLiveData: LiveData<ResponseWrapper<List<Product>>> = _cartProductsLiveData

    fun deleteProduct(product: Product) {
        viewModelScope.launch(IO) {
            homeRepository.deleteFromUserCarts(product.id)
            withContext(Main) { getUserCarts() }
        }
    }

    fun getUserCarts() {
        _cartProductsLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _cartProductsLiveData.postValue(
                homeRepository.getUserCarts()
            )
        }
    }
}