package com.ninezero.shopang.util

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class PrefsUtil @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {
    fun getSharedPrefs(): SharedPreferences {
        return sharedPrefs
    }

    var onboardingState: Boolean
        get() = sharedPrefs.getBoolean(Pref.ONBOARDING_STATE, false)
        set(value) = sharedPrefs.edit { putBoolean(Pref.ONBOARDING_STATE, value) }

    var phoneSignedIn: Boolean
        get() = sharedPrefs.getBoolean(Pref.PHONE_SIGNED_IN_STATE, false)
        set(value) = sharedPrefs.edit { putBoolean(Pref.PHONE_SIGNED_IN_STATE, value) }

    var googleSignedIn: Boolean
        get() = sharedPrefs.getBoolean(Pref.GOOGLE_SIGNED_IN_STATE, false)
        set(value) = sharedPrefs.edit { putBoolean(Pref.GOOGLE_SIGNED_IN_STATE, value) }

    var naverSignedIn: Boolean
        get() = sharedPrefs.getBoolean(Pref.NAVER_SIGNED_IN_STATE, false)
        set(value) = sharedPrefs.edit { putBoolean(Pref.NAVER_SIGNED_IN_STATE, value) }

}