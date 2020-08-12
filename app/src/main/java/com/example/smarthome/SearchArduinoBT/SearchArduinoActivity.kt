package com.example.smarthome.SearchArduinoBT

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.smarthome.ActivityStanze.HomeStanzeActivity
import com.example.smarthome.R


val TAG = "MainAct"

class SearchArduinoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_arduino)

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        //todo implementare ricerca e result da inserire in DB
        Log.d(TAG,HomeStanzeActivity.bluetoothAdapter?.address)
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String ? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice  =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    //assegna nome
                    val deviceName = device.name
                    //assegna indirizzo
                    val deviceHardwareAddress = device.address // MAC address
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }


}