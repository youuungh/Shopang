package com.ninezero.shopang.view.main.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninezero.shopang.data.repository.HomeRepository
import com.ninezero.shopang.model.Order
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    private val _ordersLiveData = MutableLiveData<ResponseWrapper<List<Order>>>()
    val ordersLiveData: LiveData<ResponseWrapper<List<Order>>> = _ordersLiveData

    init {
        getUserOrders()
    }

    private fun getUserOrders() {
        _ordersLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _ordersLiveData.postValue(
                homeRepository.getUserOrders()
            )
        }
    }

}