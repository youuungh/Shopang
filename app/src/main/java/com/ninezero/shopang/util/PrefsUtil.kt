package com.ninezero.shopang.util

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.ninezero.shopang.MyApp
import javax.inject.Inject

class PrefsUtil @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {
    fun getSharedPrefs(): SharedPreferences { return sharedPrefs }

    var onboardingState: Boolean
        get() = sharedPrefs.getBoolean(Pref.ONBOARDING_STATE, false)
        set(value) = sharedPrefs.edit {
            putBoolean(Pref.ONBOARDING_STATE, value)
        }

    var naverState: Boolean
        get() = sharedPrefs.getBoolean(Pref.NAVER_STATE, false)
        set(value) = sharedPrefs.edit {
            putBoolean(Pref.NAVER_STATE, value)
        }

}