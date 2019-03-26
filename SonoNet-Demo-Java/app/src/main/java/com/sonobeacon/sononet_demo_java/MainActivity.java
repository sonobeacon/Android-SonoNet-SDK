package com.sonobeacon.sononet_demo_java;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.sonobeacon.system.sonolib.ContentView;
import com.sonobeacon.system.sonolib.SonoNet;
import com.sonobeacon.system.sonolib.SonoNetCredentials;
import com.sonobeacon.system.sonolib.WebLink;

public class MainActivity extends Activity implements SonoNet.BeaconInfoDelegate {

    private static final int RECORD_PERMISSION_REQUEST_CODE = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private SonoNet.Control control;
    private ContentView contentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentView = findViewById(R.id.contentView);
        SonoNetCredentials credentials = new SonoNetCredentials("YOUR_API_KEY", "LOCATION_ID");  /* REPLACE WITH YOUR CREDENTIALS */
        SonoNet.initialize(this, credentials);
        control = new SonoNet.Control.Builder(this)
                .withContentView(contentView)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tryToBind();
    }

    @Override
    public void onBeaconReceivedLinkPayload(WebLink webLink) {
        Log.d("Weblink title: ", webLink.getTitle());
    }

    private void tryToBind() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {


            final boolean showLocationRationale = ActivityCompat
                    .shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION);

            final boolean showAudioRationale = ActivityCompat
                    .shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.RECORD_AUDIO);

            // permission is not granted
            if (showAudioRationale || showLocationRationale) {
                String message = "";
                if (showAudioRationale) {
                    message += getString(R.string.audioRationale);
                }
                if (showLocationRationale) {
                    message += getString(R.string.locationRationale);
                }
            } else {
                // no explanation
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.RECORD_AUDIO
                        },
                        0);
            }
        } else {
            // permission ok
            checkBluetoothAndBind();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        control.unbind();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (RECORD_PERMISSION_REQUEST_CODE == requestCode) {
            if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                checkBluetoothAndBind();
            } else {
                // do nothing so far
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                checkBluetoothAndBind();
            } else {
                control.bind(this);
            }

        }

    }


    private void checkBluetoothAndBind() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean adapterEnabled = defaultAdapter != null && defaultAdapter.isEnabled();
        if (adapterEnabled) {
            control.bind(this);
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

}
