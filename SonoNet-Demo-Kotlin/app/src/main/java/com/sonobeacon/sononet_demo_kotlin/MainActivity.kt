package com.sonobeacon.sononet_demo_kotlin

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sonobeacon.system.sonolib.SonoNet
import com.sonobeacon.system.sonolib.SonoNetCredentials
import com.sonobeacon.system.sonolib.WebLink
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

        val credentials = SonoNetCredentials("YOUR_API_KEY", "YOUR_LOCATION_ID")
        SonoNet.initialize(this, credentials)

        control = SonoNet.Control.Builder(this)
            .withContentView(contentView)
            .withMenu()
            .isDebugging
            .notifyMe()
            .build()
    }


    override fun onStart() {
        super.onStart()
        tryToBind()
    }

    override fun onStop() {
        super.onStop()
        control?.unbind()
    }


    override fun onBeaconReceivedLinkPayload(p0: WebLink?) {
        p0?.let {
            Log.d("TAG", it.title)
        }
    }

    private fun tryToBind() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            val showLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

            val showAudioRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )

            // permission is not granted
            if (showAudioRationale || showLocationRationale) {
                var message = ""
                if (showAudioRationale) {
                    message += getString(R.string.audioRationale)
                }
                if (showLocationRationale) {
                    message += getString(R.string.locationRationale)
                }
            } else {
                // no explanation
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.RECORD_AUDIO
                    ),
                    0
                )
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
                // do nothing sdo far
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
