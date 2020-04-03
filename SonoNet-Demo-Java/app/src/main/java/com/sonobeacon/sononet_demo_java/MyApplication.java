package com.sonobeacon.sononet_demo_java;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.sonobeacon.system.sonolib.RegionState;
import com.sonobeacon.system.sonolib.SonoNet;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        configureReceiver();
    }

    void configureReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RegionState.ENTER.toString());
        filter.addAction(RegionState.EXIT.toString());
        filter.addAction("ble_enter");
        filter.addAction("ble_exit");
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SonoNet.Companion.regionEvent(context, intent);
        }
    };

}
