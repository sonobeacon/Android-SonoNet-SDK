package com.sonobeacon.sononet_demo_java;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.sonobeacon.system.sonolib.core.EnterAction;
import com.sonobeacon.system.sonolib.core.SonoNet;

import java.util.Objects;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        configureReceiver();
    }

    void configureReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(EnterAction.ENTER.toString());
        filter.addAction(EnterAction.EXIT.toString());
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SonoNet.Companion.regionEvent(context, Objects.requireNonNull(intent.getAction()),
                    Objects.requireNonNull(intent.getStringExtra(getString(R.string.reminderId))));
        }
    };

}
