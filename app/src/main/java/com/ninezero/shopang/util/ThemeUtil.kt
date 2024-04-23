package com.ninezero.shopang.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StyleRes
import com.google.android.material.color.ColorContrast
import com.google.android.material.color.ColorContrastOptions
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.HarmonizedColorAttributes
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions
import com.ninezero.shopang.R

object ThemeUtil {
    fun setTheme(activity: Activity, shardPrefs: SharedPreferences) {
        val theme = shardPrefs.getString(Setting.Appearance.THEME, SettingDefault.Appearance.THEME)
        when (theme) {
            Theme.RED -> setContrastTheme(
                activity, shardPrefs,
                R.style.Theme_Shopang_Red,
                R.style.ThemeOverlay_Shopang_Red_MediumContrast,
                R.style.ThemeOverlay_Shopang_Red_HighContrast
            )
            Theme.YELLOW -> setContrastTheme(
                activity, shardPrefs,
                R.style.Theme_Shopang_Yellow,
                R.style.ThemeOverlay_Shopang_Yellow_MediumContrast,
                R.style.ThemeOverlay_Shopang_Yellow_HighContrast
            )
            Theme.GREEN -> setContrastTheme(
                activity, shardPrefs,
                R.style.Theme_Shopang_Green,
                R.style.ThemeOverlay_Shopang_Green_MediumContrast,
                R.style.ThemeOverlay_Shopang_Green_HighContrast
            )
            Theme.BLUE -> setContrastTheme(
                activity, shardPrefs,
                R.style.Theme_Shopang_Blue,
                R.style.ThemeOverlay_Shopang_Blue_MediumContrast,
                R.style.ThemeOverlay_Shopang_Blue_HighContrast
            )
            else -> {
                if (DynamicColors.isDynamicColorAvailable()) {
                    DynamicColors.applyToActivityIfAvailable(activity)
                    ColorContrast.applyToActivityIfAvailable(
                        activity,
                        ColorContrastOptions.Builder()
                            .setMediumContrastThemeOverlay(R.style.ThemeOverlay_Shopang_MediumContrast)
                            .setHighContrastThemeOverlay(R.style.ThemeOverlay_Shopang_HighContrast)
                            .build()
                    )
                } else {
                    setContrastTheme(
                        activity, shardPrefs,
                        R.style.Theme_Shopang_Green,
                        R.style.ThemeOverlay_Shopang_Green_MediumContrast,
                        R.style.ThemeOverlay_Shopang_Green_HighContrast
                    )
                }
            }
        }
    }

    private fun setContrastTheme(
        activity: Activity,
        shardPrefs: SharedPreferences,
        @StyleRes resIdStandard: Int,
        @StyleRes resIdMedium: Int,
        @StyleRes resIdHigh: Int
    ) {
        val contrast = shardPrefs.getString(
            Setting.Appearance.CONTRAST,
            SettingDefault.Appearance.CONTRAST)
        when (contrast) {
            Contrast.MEDIUM -> activity.setTheme(resIdMedium)
            Contrast.HIGH -> activity.setTheme(resIdHigh)
            else -> activity.setTheme(resIdStandard)
        }
    }
}