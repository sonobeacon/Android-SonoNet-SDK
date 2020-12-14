package com.sonobeacon.sononet_demo_kotlin

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sonobeacon.system.sonolib.core.SonoNet
import com.sonobeacon.system.sonolib.models.SonoNetCredentials
import com.sonobeacon.system.sonolib.models.WebLink
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SonoNet.BeaconInfoDelegate {

    companion object {
        const val RECORD_PERMISSION_REQUEST_CODE = 0
        const val REQUEST_ENABLE_BT = 1
    }

    private var control: SonoNet.Control? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        val credentials = SonoNetCredentials("YOUR_API_KEY")
        SonoNet.initialize(this, credentials)
        control = SonoNet.Control(
            context = this,
            contentView = contentView,
            withMenu = true,
            isDebugging = true,
            notifyMe = true,
            bluetoothOnly = false,
            showMenuEntryOnlyOnce = true
        )
    }

    override fun onStart() {
        super.onStart()
        tryToBind()
    }

    override fun onStop() {
        super.onStop()
        control?.unbind()
    }

    override fun onBeaconReceivedLinkPayload(webLink: WebLink) {
        Log.d("BEACONRECEIVED", webLink.title)
    }

    override fun onApiDeprecated() {
        // get notified when the api is deprecated
        // in this case, stop the sdk from initializing
    }

    private fun tryToBind() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) ||
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {


            val showCoarseLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

            val showFineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

            val showAudioRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )

            var showbackgroundLocationRationale = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                showbackgroundLocationRationale = ActivityCompat
                    .shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
            }

            // permission is not granted
            if (showAudioRationale || showCoarseLocationRationale || showFineLocationRationale) {
                var message = ""
                if (showAudioRationale) {
                    message += getString(R.string.audioRationale)
                }
                if (showCoarseLocationRationale) {
                    message += getString(R.string.locationRationale)
                }
                if (showFineLocationRationale) {
                    message += getString(R.string.locationRationale)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && showbackgroundLocationRationale) {
                    message += getString(R.string.locationRationale)
                }
            } else {
                // no explanation
                var array = arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    array += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                }
                ActivityCompat.requestPermissions(this, array, 0)
            }
        } else {
            // permission ok
            checkBluetoothAndBind()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (RECORD_PERMISSION_REQUEST_CODE == requestCode) {
            if (grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                checkBluetoothAndBind()
            } else {
                // do nothing so far
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                checkBluetoothAndBind()
            } else {
                control?.bind(this)
            }

        }
    }

    private fun checkBluetoothAndBind() {
        val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        val adapterEnabled = defaultAdapter != null && defaultAdapter.isEnabled
        if (adapterEnabled) {
            control?.bind(this)
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }
}