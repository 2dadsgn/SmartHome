package com.example.smarthome.StatHome

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.BoringLayout
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.smarthome.ActivityStanze.HomeStanzeActivity
import com.example.smarthome.ActivityStanze.fragHomeStanze
import com.example.smarthome.R
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.createNewArduino.newArduinoActivty
import com.example.smarthome.viewModel.HomeStatModel
import kotlinx.android.synthetic.main.activity_statistiche.*
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.UTFDataFormatException
import java.util.*

val TAG: String = "BTinfo"

class StatisticheActivity : AppCompatActivity() {

    var valore: String = ""


    companion object {

        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")        //HC-05

        //var m_myUUID: UUID = UUID.fromString("19b10000-e8f2-537e-4f6c-d104768a1214")             //arduino ble
        var m_bluetoothSocket: BluetoothSocket? = null

        //lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        lateinit var contesto: Context
        var back: Boolean = false
    }


    private fun sendCommand(input: String) {
        if (m_isConnected && m_bluetoothSocket != null) {
            try {
                //manda comando in bytearray
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                m_bluetoothSocket!!.outputStream.flush()
                Log.d(TAG, "Bluetooth command sent succesfully!")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d(TAG, "Bluetooth command not sent!")
            }
        } else {
            Log.d(TAG, "bluetooth socket is null!!")
        }



    }

    //funzion per mandare comando di aggiornamento e ricevere i dati
    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getData() {

        sendCommand("connesso")
        val buffer = ByteArray(1024)
        var bytes: Int
        var readMessage: String = ""

        //Log.d(TAG, m_bluetoothSocket!!.inputStream.read(buffer,2,2).toString())

        if (m_isConnected) {

            try {
                Log.d(TAG, "getBTdata entra")

                //legge da buffer stringa in input e assegna numero byte ricevuti
                 bytes = m_bluetoothSocket!!.inputStream.read(buffer)

                //conversione da byte array a Stringa
                readMessage = readMessage + String(buffer, 0, bytes)

                Log.d(TAG, readMessage)
                Log.d(TAG, "getBTdata esce")

            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d(TAG, "getBTdata fallito ")
            }
            Log.d(TAG, "getBTdata uscito dal try ")

        } else {
            Log.d(TAG, "non connesso")
            val toast = Toast.makeText(
                contesto, "Non connesso",
                Toast.LENGTH_LONG
            )
            toast.show()
        }
        Log.d(TAG, "getBTData terminato")

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
        private var connectSuccess: Boolean = false
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            //m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            while (m_bluetoothSocket == null || !m_isConnected || !back) {

                try {
                    if (m_address == "null" || m_address == null || m_address == "") {
                        Log.d(TAG, "address null")
                        connectSuccess = false
                        break
                    } else if (m_bluetoothSocket == null || !m_isConnected) {

                        Log.d(TAG, "Creazione rfcon")

                        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                        val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)

                        //bluetooth socket null poichè non crea rfconsocket connection
                        m_bluetoothSocket =
                            device.createInsecureRfcommSocketToServiceRecord(m_myUUID)


                        //cancella la scoperta di dispositivi
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                        //effettua connessione
                        m_bluetoothSocket!!.connect()
                        //imposta valore true per PostExecut per mostrare toast connessione effettuata
                        connectSuccess = true
                        break
                    }
                } catch (e: Exception) {
                    connectSuccess = false
                    e.printStackTrace()
                    Log.d(TAG, "RFCON not created")
                }
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
                val toast = Toast.makeText(
                    contesto, "Connection unsuccesful",
                    Toast.LENGTH_LONG
                )
                toast.show()

            } else {
                m_isConnected = true
                val toast = Toast.makeText(
                    contesto, "Connected",
                    Toast.LENGTH_LONG
                )
                //invia comando connesso ad arduino
                //m_bluetoothSocket!!.outputStream.write("c".toByteArray())
                toast.show()

            }
            //m_progress.dismiss()
        }
    }

    fun setUI() {
        Log.d(TAG, "entra in set UI")
        //lista di view
        val views =
            mutableListOf(textViewTempo, tempInput, switch1, switch2, textViewSeekbar, seekBar3)
        //lista per dati
        var stringheDati: MutableList<String> = mutableListOf()

        //indici per individuare views
        val textviewseek: Int = 4
        val seek: Int = 5
        val textTemp: Int = 0
        val tempInput: Int = 1
        val switch1: Int = 2
        val switch2: Int = 3

        var fine: Int = valore.indexOf(';', 0)
        var pos: Int = 0
        var med: Int = 0
        var i: Int = 0
        var tmp: Int

        while (pos != -1) {
            Log.d(TAG, "entra in ciclo while UI")
            pos = valore.indexOf('-', med)
            Log.d(TAG, "valore di pos: $pos")
            //se il valore di pos al primo elemento è zero allora significa
            //che è stato inviato dati solo per un sensore
            if (i == 0 && pos == -1) {
                stringheDati.add(i, valore.subSequence(0, fine).toString())
            } else {
                stringheDati.add(i, valore.subSequence(med, pos).toString())
            }

            med = pos
            Log.d(TAG, "stringheDAti: " + stringheDati[i])
            i++
        }

        //ottiene range
        var indices = stringheDati.indices

        for (i in indices) {
            pos = stringheDati[i].indexOf('(')
            //se non viene specificato nulla tra parentesi allora non sono presenti
            if (pos == -1) {
                pos = stringheDati[i].indexOf(':')
            }
            var view = stringheDati[i].subSequence(i, pos).toString()
            if (view.startsWith(' ')) {
                view = view.subSequence(1, view.lastIndex + 1).toString()
            }

            Log.d(TAG, "-" + view + "-")
            if (view == "textTemp") {
                //7 var Temperatura : String = valore.subSequence(0,pos).toString() + "° C"
                //tempInput.setText(Temperatura)
            } else if (view == "stat") {

            } else if (view == "switch1") {

            } else if (view == "switch2") {

            } else if (view == "seekbar") {
                Log.d(TAG, "imposta seekbar ")
                pos = stringheDati[i].indexOf('(')

                var next = stringheDati[i].indexOf(')')

                textViewSeekbar.setText(stringheDati[i].subSequence(pos + 1, next).toString())
                //seekBar3.progress = ( stringheDati[i].subSequence(pos,stringheDati[i].lastIndex).toString().toInt() )

            }
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contesto = applicationContext


        setContentView(R.layout.activity_statistiche)

        //Crea DB
        var db = AppClassDatabase.get(application)

        CoroutineScope(Dispatchers.Default).launch {
            Log.d(TAG, HomeStanzeActivity.ActualStanza!!)
            //imposta nome stanza
            nomeStanza.setText(HomeStanzeActivity.ActualStanza.toString())

            //ottiene codice arduino stanza
            var code = db.stanzaDao().findByName(HomeStanzeActivity.ActualStanza!!).ArduinoCode

            //ottiene address arduino
            var address = db.arduinoDao().getOneWithID(code).address

            //impostat address in companion object
            try {
                m_address = address!!
            } catch (ex: Exception) {
                m_address = "null"
                Log.d(TAG, ex.toString())
            }

        }

        //effettua connessione all'arduino
        ConnectToDevice(this).execute()


        //refresh per ottenere dati
        refresh.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {

                getData()
                //setUI()
            }
        }

        //azione per switch1
        switch1.setOnClickListener {
            if (switch1.isChecked) {
                sendCommand("${switch1.text}:acceso")
            } else {
                sendCommand("${switch1.text}:spento")
            }
        }

        //azione per switch2
        switch2.setOnClickListener {
            if (switch2.isChecked) {
                sendCommand("${switch2.text}:acceso")
            } else {
                sendCommand("${switch2.text}:spento")
            }
        }

        //seekbar listener
        seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {


            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                //stampa in toast per utente
                Toast.makeText(applicationContext, seekBar3.progress.toString(), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // manda valore ad arduino
                Log.d(TAG, "${textViewSeekbar.text}:${seekBar3.progress};")
                sendCommand("tenda:${seekBar3.progress}")

            }
        })


    }


    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        back = true


        try {
            disconnect()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        finish()
        intent = Intent(this, HomeStanzeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)

    }


}