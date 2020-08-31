package com.sonobeacon.sononet_demo_kotlin

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.sonobeacon.system.sonolib.core.EnterAction
import com.sonobeacon.system.sonolib.core.SonoNet

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
        filter.addAction(EnterAction.ENTER.toString())
        filter.addAction(EnterAction.EXIT.toString())
        registerReceiver(broadcastReceiver, filter)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            intent?.action.let {
                SonoNet.regionEvent(context, it ?: "", intent?.getStringExtra(getString(R.string.reminderId)) ?: "")
            }
        }
    }

}
