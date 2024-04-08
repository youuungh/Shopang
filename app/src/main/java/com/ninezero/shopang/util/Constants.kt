package com.ninezero.shopang.util

import androidx.appcompat.app.AppCompatDelegate

object Constants {
    const val USER_PREFS = "user_prefs"

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
        const val ONBOARDING_SHOWN ="onboarding_shown"
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
}