package com.example.smarthome.StatHome

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.smarthome.ActivityStanze.HomeStanzeActivity
import com.example.smarthome.ActivityStanze.fragHomeStanze
import com.example.smarthome.R
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.createNewArduino.newArduinoActivty
import com.example.smarthome.viewModel.HomeStatModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

val TAG:String= "BTinfo"
class StatisticheActivity : AppCompatActivity() {


    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        lateinit var contesto : Context
    }



    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())

                Log.d(TAG,"Bluetooth command sent succesfully!")
            } catch(e: IOException) {
                e.printStackTrace()
                Log.d(TAG,"Bluetooth command not sent!")
            }
        }
        else{
            Log.d(TAG,"bluetooth socket is null!!")
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if(m_address=="null"){
                    Log.d(TAG,"address null")
                    connectSuccess = false

                }
                else if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
                val toast = Toast.makeText(
                    contesto,"Connection unsuccesful",
                    Toast.LENGTH_LONG)
                toast.show()
            } else {
                m_isConnected = true
                val toast = Toast.makeText(
                    contesto,"Connected",
                    Toast.LENGTH_LONG)
                toast.show()

            }
            m_progress.dismiss()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contesto = applicationContext

        setContentView(R.layout.activity_statistiche)

        //Crea DB
        var db = AppClassDatabase.get(application)

        CoroutineScope(Dispatchers.Default).launch {
            Log.d(TAG,HomeStanzeActivity.ActualStanza!!)
            //ottiene codice arduino stanza
            var code = db.stanzaDao().findByName(HomeStanzeActivity.ActualStanza!!).ArduinoCode

            //ottiene address arduino
            var address = db.arduinoDao().getOneWithID(code).address

            //impostat address in companion object
            try {
                m_address = address!!
            }
            catch (ex : Exception){
                m_address="null"
                Log.d(TAG,ex.toString())
            }

        }



        ConnectToDevice(this).execute()

        sendCommand("ciao")

        Log.d(TAG,"funzione sendcommand richiamata")



    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        intent = Intent(this, HomeStanzeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        disconnect()
    }



}