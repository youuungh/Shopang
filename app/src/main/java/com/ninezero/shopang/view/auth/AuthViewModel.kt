package com.ninezero.shopang.view.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthCredential
import com.ninezero.shopang.data.repository.AuthRepository
import com.ninezero.shopang.util.AuthState
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _authLiveData = MutableLiveData<AuthState>()
    val authLiveData: LiveData<AuthState>
        get() = _authLiveData

    private val _authStatusLiveData = MutableLiveData<ResponseWrapper<Unit?>>()
    val authStatusLiveData: LiveData<ResponseWrapper<Unit?>>
        get() = _authStatusLiveData

    private val _userInfoLiveData = MutableLiveData<ResponseWrapper<String>>()
    val userInfoLiveData: LiveData<ResponseWrapper<String>>
        get() = _userInfoLiveData

    fun setAuthLiveData(authState: AuthState) { _authLiveData.value = authState }
    fun setUserInfoLiveData() { _userInfoLiveData.value = ResponseWrapper.Idle() }
    fun signInAuthCredential(credential: PhoneAuthCredential) {
        _authStatusLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _authStatusLiveData.postValue(authRepository.signInWithCredential(credential))
        }
    }
    fun authCallBack() = authRepository.authCallBack(_authLiveData)
}