package com.ninezero.shopang.view.main.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninezero.shopang.data.repository.HomeRepository
import com.ninezero.shopang.model.Order
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    private val _orderProductsLiveData = MutableLiveData<ResponseWrapper<Order>>()
    val orderProductsLiveData: LiveData<ResponseWrapper<Order>> = _orderProductsLiveData

    fun submitUserOrder(cartList: Array<Product>, userAddress: String, totalPrice: Int) {
        _orderProductsLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _orderProductsLiveData.postValue(
                homeRepository.submitUserOrder(cartList, userAddress, totalPrice)
            )
        }
    }
}