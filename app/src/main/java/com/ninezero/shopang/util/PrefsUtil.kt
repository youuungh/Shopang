package com.ninezero.shopang.util

import android.app.Activity
import android.content.SharedPreferences
import androidx.annotation.StyleRes
import androidx.core.content.edit
import com.google.android.material.color.ColorContrast
import com.google.android.material.color.ColorContrastOptions
import com.google.android.material.color.DynamicColors
import com.ninezero.shopang.MyApplication
import com.ninezero.shopang.R

object PrefsUtil {
    private val sharedPrefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
        MyApplication.getContext())

    fun getSharedPreferences(): SharedPreferences {
        return sharedPrefs
    }

    var onboardShown: Boolean
        get() = sharedPrefs.getBoolean(Constants.Pref.ONBOARDING_SHOWN, false)
        set(value) = sharedPrefs.edit {
            putBoolean(Constants.Pref.ONBOARDING_SHOWN, value)
        }
}