package com.ninezero.shopang.view.auth

import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.ninezero.shopang.data.repository.AuthRepository
import com.ninezero.shopang.util.AuthState
import com.ninezero.shopang.util.COUNT_DOWN_DURATION_MILLIS
import com.ninezero.shopang.util.COUNT_DOWN_INTERVAL
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _googleAuthLiveData = MutableLiveData<ResponseWrapper<Unit?>>()
    val googleAuthLiveData
        get() = _googleAuthLiveData

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean>
        get() = _isTimerRunning

    private val _timerSharedFlow = MutableSharedFlow<Long>()
    val timerSharedFlow: SharedFlow<Long>
        get() = _timerSharedFlow.asSharedFlow()

    private var countDownTimer: CountDownTimer? = null

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun authCallBack() = authRepository.authCallBack(_authLiveData)

    fun setAuthLiveData(authState: AuthState) { _authLiveData.value = authState }

    fun startCountDown() {
        countDownTimer?.cancel()
        _isTimerRunning.value = true
        countDownTimer = object : CountDownTimer(
            COUNT_DOWN_DURATION_MILLIS,
            COUNT_DOWN_INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {
                viewModelScope.launch { _timerSharedFlow.emit(millisUntilFinished) }
            }

            override fun onFinish() {
                _isTimerRunning.value = false
                viewModelScope.launch { _timerSharedFlow.emit(0) }
            }
        }.start()
    }

    fun signInAuthCredential(credential: PhoneAuthCredential) {
        _authStatusLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _authStatusLiveData.postValue(authRepository.signInWithCredential(credential))
        }
    }

    fun uploadUserInfo(userName: String, profileImageUri: Uri?, isUpdate: Boolean) {
        _userInfoLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch(IO) {
            _userInfoLiveData.postValue(authRepository.uploadUserInfo(userName, profileImageUri, isUpdate))
        }
    }

    fun processGoogleSignInResult(task: Task<GoogleSignInAccount>, errorMsg: String) {
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            fAuthWithGoogle(credential)
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Google 로그인 실패 ${e.message}", e)
            _googleAuthLiveData.value = ResponseWrapper.Error(errorMsg)
        }
    }

    private fun fAuthWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            _googleAuthLiveData.postValue(authRepository.signInWithCredential(credential))
        }
    }
}