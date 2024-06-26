package com.ninezero.shopang.view.auth

import android.app.Application
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.ninezero.shopang.data.repository.AuthRepository
import com.ninezero.shopang.model.UserInfo
import com.ninezero.shopang.util.AuthState
import com.ninezero.shopang.util.COUNT_DOWN_DURATION_MILLIS
import com.ninezero.shopang.util.COUNT_DOWN_INTERVAL
import com.ninezero.shopang.util.GOOGLE
import com.ninezero.shopang.util.NAVER
import com.ninezero.shopang.util.NetworkStatusViewModel
import com.ninezero.shopang.util.PHONE
import com.ninezero.shopang.util.PrefsUtil
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
    private val application: Application,
    private val prefsUtil: PrefsUtil,
    private val authRepository: AuthRepository
) : ViewModel() {

    private var countDownTimer: CountDownTimer? = null

    private val _authLiveData = MutableLiveData<AuthState>()
    val authLiveData: LiveData<AuthState>
        get() = _authLiveData

    private val _authStatusLiveData = MutableLiveData<ResponseWrapper<Unit?>>()
    val authStatusLiveData: LiveData<ResponseWrapper<Unit?>>
        get() = _authStatusLiveData

    private val _authInfoLiveData = MutableLiveData<ResponseWrapper<String>>()
    val authInfoLiveData: LiveData<ResponseWrapper<String>>
        get() = _authInfoLiveData

    private val _googleAuthLiveData = MutableLiveData<ResponseWrapper<Unit?>>()
    val googleAuthLiveData
        get() = _googleAuthLiveData

    private val _isNetworkConnected = MutableLiveData<Boolean>()
    val isNetworkConnected: LiveData<Boolean> = _isNetworkConnected

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean>
        get() = _isTimerRunning

    private val _timerSharedFlow = MutableSharedFlow<Long>()
    val timerSharedFlow: SharedFlow<Long>
        get() = _timerSharedFlow.asSharedFlow()

    init {
        val networkConnectionLiveData = NetworkStatusViewModel(application)
        networkConnectionLiveData.observe(ProcessLifecycleOwner.get()) { isConnected ->
            _isNetworkConnected.value = isConnected
        }
    }

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun checkSignInPlatform(platform: String) {
        when (platform) {
            PHONE -> prefsUtil.phoneSignedIn = true
            GOOGLE -> prefsUtil.googleSignedIn = true
            NAVER -> prefsUtil.naverSignedIn = true
        }
    }

    fun authCallBack() = authRepository.authCallBack(_authLiveData)

    fun setAuthLiveData(authState: AuthState) {
        _authLiveData.value = authState
    }

    fun resetUserInfoLiveData() {
        _authInfoLiveData.value = ResponseWrapper.Idle()
    }

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

    fun uploadUserInfo(
        platform: String,
        userName: String,
        userAddress: String?,
        profileImageUri: Uri?,
        isUpload: Boolean,
        isUpdate: Boolean
    ) {
        _authInfoLiveData.value = ResponseWrapper.Loading()
        viewModelScope.launch {
            _authInfoLiveData.postValue(
                authRepository.uploadUserInfo(
                    platform,
                    userName,
                    userAddress,
                    profileImageUri,
                    isUpload,
                    isUpdate
                )
            )
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