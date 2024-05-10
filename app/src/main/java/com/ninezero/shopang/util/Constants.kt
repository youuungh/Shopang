package com.ninezero.shopang.util

import androidx.appcompat.app.AppCompatDelegate

const val LOADING = "loading"
const val COUNT_DOWN_DURATION_MILLIS = 60000L
const val COUNT_DOWN_INTERVAL = 1000L
const val MAX_ATTEMPTS = 5
const val USER_COLLECTION = "user_collection"
const val PHONE = "phone"
const val GOOGLE = "google"
const val NAVER = "naver"
const val FUNCTIONS_URL = "https://asia-northeast3-shopang-48ecf.cloudfunctions.net/"


object SettingDefault {
    object Appearance {
        const val THEME = ""
        const val DARK_MODE = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        const val CONTRAST = Contrast.STANDARD
    }
}
object Setting {
    object Appearance {
        const val THEME = "app_theme"
        const val DARK_MODE = "dark_mode"
        const val CONTRAST = "contrast"
    }
}
object Argument {
    const val ONBOARDING_POSITION = "onboarding_position"
}
object Pref {
    const val IS_FIRST_OPENED = "is_first_opened"
    const val ONBOARDING_STATE = "onboarding_state"
    const val PHONE_SIGNED_IN_STATE = "phone_signed_in_state"
    const val GOOGLE_SIGNED_IN_STATE = "google_signed_in_state"
    const val NAVER_SIGNED_IN_STATE = "naver_signed_in_state"
}
object Theme {
    const val DYNAMIC = "dynamic"
    const val RED = "red"
    const val YELLOW = "yellow"
    const val GREEN = "green"
    const val BLUE = "blue"
}
object Contrast {
    const val STANDARD = "standard"
    const val MEDIUM = "medium"
    const val HIGH = "high"
}