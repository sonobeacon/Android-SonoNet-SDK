package com.sonobeacon.sononet_demo_java;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sonobeacon.system.sonolib.content.ContentView;
import com.sonobeacon.system.sonolib.core.SonoNet;
import com.sonobeacon.system.sonolib.models.SonoNetCredentials;
import com.sonobeacon.system.sonolib.models.WebLink;

public class MainActivity extends Activity implements SonoNet.BeaconInfoDelegate {

    private static final int RECORD_PERMISSION_REQUEST_CODE = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private SonoNet.Control control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentView contentView = findViewById(R.id.contentView);

        /* REPLACE WITH YOUR API KEY */
        SonoNetCredentials credentials = new SonoNetCredentials("YOUR_API_KEY");
        SonoNet.Companion.initialize(this, credentials);

        control = new SonoNet.Control(this,
                contentView,
                true,
                true,
                true,
                false,
                true,
                14f,
                "000000"
        );
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

    @Override
    public void onApiDeprecated() {
        // get notified when the api is deprecated
        // in this case, stop the sdk from initializing
    }

    private void tryToBind() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            final boolean showCoarseLocationRationale = ActivityCompat
                    .shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION);

            final boolean showFineLocationRationale = ActivityCompat
                    .shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION);

            final boolean showAudioRationale = ActivityCompat
                    .shouldShowRequestPermissionRationale(
                            this, Manifest.permission.RECORD_AUDIO);

            boolean showBackgroundLocationRationale = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                showBackgroundLocationRationale = ActivityCompat
                        .shouldShowRequestPermissionRationale(
                                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }

            // permission is not granted
            if (showAudioRationale || showCoarseLocationRationale) {
                String message = "";
                if (showAudioRationale) {
                    message += getString(R.string.audioRationale);
                }
                if (showCoarseLocationRationale) {
                    message += getString(R.string.locationRationale);
                }
                if (showFineLocationRationale) {
                    message += getString(R.string.locationRationale);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && showBackgroundLocationRationale) {
                    message += getString(R.string.locationRationale);
                }
            } else {
                // no explanation
                String[] array = new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                };
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    array[3] = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
                }
                ActivityCompat.requestPermissions(this, array, 0);
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