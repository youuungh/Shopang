package com.ninezero.shopang

import android.app.Application
import com.ninezero.shopang.util.PrefsUtil
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application() {

    @Inject
    lateinit var prefsUtil: PrefsUtil

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun getContext(): MyApplication {
            return instance!!
        }
    }
}