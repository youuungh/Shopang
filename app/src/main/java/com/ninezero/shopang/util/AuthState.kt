package com.ninezero.shopang.util

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

sealed class AuthState {
    data class SuccessWithCode(
        val id: String,
        val token: PhoneAuthProvider.ForceResendingToken
    ): AuthState()
    data class SuccessWithCredential(val data: PhoneAuthCredential): AuthState()
    data class Error(val error: FirebaseException): AuthState()
    data object Loading: AuthState()
    data object Idle: AuthState()
}