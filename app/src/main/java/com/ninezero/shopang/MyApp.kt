package com.ninezero.shopang

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: MyApp? = null

        fun getContext(): MyApp {
            return instance!!
        }
    }
}