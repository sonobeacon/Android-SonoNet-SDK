package com.sonobeacon.sononet_demo_kotlin

import android.Manifest.permission.*
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sonobeacon.system.sonolib.core.SonoNet
import com.sonobeacon.system.sonolib.domain.models.SonoNetCredentials
import com.sonobeacon.system.sonolib.domain.models.WebLink
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SonoNet.BeaconInfoDelegate {

    companion object {
        const val REQUEST_ENABLE_BT = 1
    }

    private var control: SonoNet.Control? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { pair: Map<String, Boolean> ->
            if (pair.containsValue(false)) {
                Log.e("ERROR","NOT ALL PERMISSIONS GRANTED")
            } else {
                checkBluetoothAndBind()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        val credentials = SonoNetCredentials("40d16ff6-c7d1-45e2-babf-8a394ad905c6")
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
//        tryToBind()
        handlePermissions()
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

    private fun handlePermissions() {
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
            checkBluetoothAndBind()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(RECORD_AUDIO, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
            )
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