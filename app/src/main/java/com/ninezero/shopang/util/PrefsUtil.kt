package com.ninezero.shopang.util

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.ninezero.shopang.MyApp

object PrefsUtil {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext())

    fun getSharedPrefs(): SharedPreferences { return sharedPrefs }

    var onBoardingState: Boolean
        get() = sharedPrefs.getBoolean(Constants.Pref.ONBOARDING_SHOWN, false)
        set(value) = sharedPrefs.edit {
            putBoolean(Constants.Pref.ONBOARDING_SHOWN, value)
        }
}