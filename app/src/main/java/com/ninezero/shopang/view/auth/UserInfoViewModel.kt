package com.ninezero.shopang.view.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninezero.shopang.data.repository.AuthRepository
import com.ninezero.shopang.model.UserInfo
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _userInfoLiveData = MutableLiveData<ResponseWrapper<UserInfo>>()
    val userInfoLiveData : LiveData<ResponseWrapper<UserInfo>>
        get() = _userInfoLiveData

    init {
        getUserInfo()
    }

    fun updateUserAddress(address: String) {
        viewModelScope.launch(IO) {
            authRepository.updateUserAddress(address)
        }
    }

    private fun getUserInfo() {
        viewModelScope.launch(IO) {
            authRepository.getUserInfo(_userInfoLiveData)
        }
    }

}