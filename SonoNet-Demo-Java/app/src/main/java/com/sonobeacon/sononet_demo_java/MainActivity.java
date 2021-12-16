package com.sonobeacon.sononet_demo_java;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.sonobeacon.system.sonolib.content.ContentView;
import com.sonobeacon.system.sonolib.core.SonoNet;
import com.sonobeacon.system.sonolib.domain.models.SonoNetCredentials;
import com.sonobeacon.system.sonolib.domain.models.WebLink;

public class MainActivity extends ComponentActivity implements SonoNet.BeaconInfoDelegate {

    private static final int REQUEST_ENABLE_BT = 1;
    private SonoNet.Control control;

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.containsValue(false)) {
                    Log.e("ERROR", "NOT ALL PERMISSIONS GRANTED");
                } else {
                    checkBluetoothAndBind();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentView contentView = findViewById(R.id.contentView);

        /* REPLACE WITH YOUR API KEY */
        SonoNetCredentials credentials = new SonoNetCredentials("YOUR_API_KEY");
        SonoNet.Companion.initialize(this, credentials);

//        control = new SonoNet.Control(getApplicationContext());

        control = new SonoNet.Control(getApplicationContext(),
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
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
        ) + ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
        ) + ContextCompat.checkSelfPermission(
                this,
                RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkBluetoothAndBind();
        } else {
            requestPermissionLauncher.launch(
                    new String[] { RECORD_AUDIO, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION }
            );
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        control.unbind();
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