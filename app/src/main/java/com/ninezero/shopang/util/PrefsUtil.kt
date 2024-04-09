package com.ninezero.shopang.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

object PrefsUtil {

    private lateinit var sharedPrefs: SharedPreferences

    fun init(context: Context) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPrefs
    }

    var onboardShown: Boolean
        get() = sharedPrefs.getBoolean(Constants.Pref.ONBOARDING_SHOWN, false)
        set(value) = sharedPrefs.edit {
            putBoolean(Constants.Pref.ONBOARDING_SHOWN, value)
        }
}