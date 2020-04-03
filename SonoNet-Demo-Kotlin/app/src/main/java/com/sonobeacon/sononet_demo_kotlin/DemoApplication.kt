package com.sonobeacon.sononet_demo_kotlin

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.sonobeacon.system.sonolib.RegionState
import com.sonobeacon.system.sonolib.SonoNet

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

    override fun onCreate() {
        super.onCreate()
        configureReceiver()
    }

    private fun configureReceiver() {
        val filter = IntentFilter()
        filter.addAction(RegionState.ENTER.toString())
        filter.addAction(RegionState.EXIT.toString())
        filter.addAction("ble_enter")
        filter.addAction("ble_exit")
        registerReceiver(broadcastReceiver, filter)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                SonoNet.regionEvent(it, intent)
            }
        }
    }

}
