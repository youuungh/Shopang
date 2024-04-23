package com.ninezero.shopang.util

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.ninezero.shopang.MyApp

object PrefsUtil {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext())

    fun getSharedPrefs(): SharedPreferences { return sharedPrefs }
    fun updateUserLoggedInState(fAuth: FirebaseAuth) { isUserLoggedIn = fAuth.currentUser != null }

    var onBoardingState: Boolean
        get() = sharedPrefs.getBoolean(Pref.ONBOARDING_SHOWN, false)
        set(value) = sharedPrefs.edit {
            putBoolean(Pref.ONBOARDING_SHOWN, value)
        }

    var isUserLoggedIn: Boolean
        get() = sharedPrefs.getBoolean(Pref.USER_LOGGED_IN, false)
        set(value) = sharedPrefs.edit {
            putBoolean(Pref.USER_LOGGED_IN, value)
        }
}