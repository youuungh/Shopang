package com.ninezero.shopang.util

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class PrefsUtil @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    fun getSharedPreferences(): SharedPreferences {
        return sharedPrefs
    }

    var onboardShown: Boolean
        get() = sharedPrefs.getBoolean(Constants.Pref.ONBOARDING_SHOWN, false)
        set(value) = sharedPrefs.edit {
            putBoolean(Constants.Pref.ONBOARDING_SHOWN, value)
        }
}