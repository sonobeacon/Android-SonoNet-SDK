package com.sonobeacon.sononet_demo_kotlin

import android.app.Application
import android.content.Context

class DemoApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: DemoApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
/*
    override fun onCreate() {
        super.onCreate()
        // initialize for any

        // Use ApplicationContext.
        // example: SharedPreferences etc...
    //    val context: Context = com.sonobeacon.sononet_demo_kotlin.DemoApplication.applicationContext()
    //    SonoSystem.initialize(com.sonobeacon.sononet_demo_kotlin.DemoApplication.applicationContext())
    }
    */

}
