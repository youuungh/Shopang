package com.ninezero.shopang

import android.app.Application
import com.ninezero.shopang.util.PrefsUtil
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PrefsUtil.init(this)
    }
}