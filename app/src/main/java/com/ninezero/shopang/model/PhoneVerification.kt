package com.ninezero.shopang.model

import android.os.Parcelable
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneVerification(
    val id: String,
    val token: PhoneAuthProvider.ForceResendingToken,
    val phoneNumber: String
): Parcelable