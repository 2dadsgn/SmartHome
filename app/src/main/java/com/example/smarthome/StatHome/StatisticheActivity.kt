package com.example.smarthome.StatHome

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.core.view.isVisible
import com.example.smarthome.ActivityStanze.HomeStanzeActivity
import com.example.smarthome.ActivityStanze.fragHomeStanze
import com.example.smarthome.R
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.createNewArduino.newArduinoActivty
import com.example.smarthome.viewModel.HomeStatModel
import kotlinx.android.synthetic.main.activity_statistiche.*
import kotlinx.android.synthetic.main.activity_statistiche.view.*
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.android.synthetic.main.fragment_scelta_stanza.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UTFDataFormatException
import java.util.*
import java.util.concurrent.Executor
import kotlin.properties.Delegates.observable

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

        class adapter {
            var is_Connected: String by observable("false") { _, oldValue, newValue ->
                onTitleChanged?.invoke(oldValue, newValue)
            }

            var onTitleChanged: ((String, String) -> Unit)? = null
        }

        val adattatore = adapter()
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

    //funzion per mandare comando di aggiornamento e ricevere i dati secondo android developer
    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getData() {
        //manda comando di connessione e per poi ricevere dati
        sendCommand("connesso")

        var numBytes: Int // bytes returned from read()
        val mmInStream: InputStream = m_bluetoothSocket!!.inputStream
        val mmOutStream: OutputStream = m_bluetoothSocket!!.outputStream
        val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream
        var readMessage: String = ""


        // Keep listening to the InputStream until the string received contains a ';'
        while (!(readMessage.contains(';'))) {
            // Read from the InputStream.
            numBytes = try {
                mmInStream.read(mmBuffer)

            } catch (e: IOException) {
                Log.d(TAG, "Input stream was disconnected", e)
                break
            }
            //conversione da byte array a Stringa
            readMessage = readMessage + String(mmBuffer, 0, numBytes)

        }
        valore = readMessage
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



    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>(){
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

                adattatore.is_Connected = "true"
                toast.show()

            }

        }
    }

    fun setUI() {

        //lista per dati
        var stringheDati: MutableList<String> = mutableListOf()

        var fine: Int = valore.indexOf(';', 0)
        var pos: Int = 0
        var i: Int = 0
        var tmp: Int


        //ciclo con il quale spezzo la stringa valore in substring per ogni sensore
        // e inserisco in array
        while (pos != -1) {
            pos = valore.indexOf('-')

            //se il valore di pos al primo elemento è zero allora significa
            //che è stato inviato dati solo per un sensore
            if (pos == -1) {
                var fine: Int = valore.indexOf(';')
                stringheDati.add(i, valore.substring(0, fine))
                //taglia la stringa
                valore = ""
            } else {
                //altrimenti abbiamo più dati
                stringheDati.add(i, valore.subSequence(0, pos).toString())
                //taglia la stringa
                valore = valore.substring(++pos)
            }
            i++
        }

        //ottiene range
        var indices = stringheDati.indices

        //ciclo for per inserire le singole stringhe nei view appositi
        for (i in indices) {
            pos = stringheDati[i].indexOf('(')
            //se non viene specificato nulla tra parentesi allora non sono presenti
            if (pos == -1) {
                pos = stringheDati[i].indexOf(':')
            }
            var view = stringheDati[i].substring(0, pos)
            if (view.startsWith(' ')) {
                view = view.subSequence(1, view.lastIndex + 1).toString()
            }

            //a seconda della temperatura cambia colore testo
            if (view == "temperatura") {
                var Temperatura: String = stringheDati[i].substring(++pos)
                var  valoreTemp:Int = Temperatura.toInt()
                Temperatura = Temperatura +  "° C"
                tempInput.setText(Temperatura)

                if (valoreTemp>= 20 && valoreTemp<=25){
                    tempInput.setTextColor(Color.parseColor("#ffdcc7"));
                }
                else if (valoreTemp>25 && valoreTemp<=30){
                    tempInput.setTextColor(Color.parseColor("#fa8282"));
                }
                else if(valoreTemp>30){
                    tempInput.setTextColor(Color.parseColor("#fa6464"))
                }
                else{
                    tempInput.setTextColor(Color.parseColor("#a8e1ff"));
                }

            } else if (view == "stat") {

            } else if (view == "switch1") {

            } else if (view == "switch2") {

            } else if (view == "seekbar") {

                pos = stringheDati[i].indexOf('(')

                var next = stringheDati[i].indexOf(')')

                textViewSeekbar.setText(stringheDati[i].substring(++pos, next))
                next = next + 2
                seekBar3.progress = stringheDati[i].substring(next).toInt()
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
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

        //invia comando connesso per poi ricevere dati
        //attenzione arduino impiega alcuni secondi per inviare stringa
        sendCommand("connesso")

        //listener per variabile connesso
        //alla connessione rimuove la sfocatura e aggiorna i dati
        adattatore.onTitleChanged = { oldValue, newValue->

            constraintActivityStatistiche.foreground = null
            progressBar3.isVisible = false

            CoroutineScope(Dispatchers.Main).launch {
                getData()
                setUI()
            }


        }

        //azione di aggiornamenti dati ogni



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
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
               //do nothing
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
               //do nothing
            }


            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // manda valore ad arduino
                Log.d(TAG, "${textViewSeekbar.text}:${seekBar3.progress};")
                sendCommand("${textViewSeekbar.text}:${seekBar3.progress}")
                //stampa in toast per utente
                Toast.makeText(applicationContext, seekBar3.progress.toString(), Toast.LENGTH_SHORT)
                    .show()

            }
        })

    }


    override fun onStart() {
        super.onStart()
    }

    //interrompe connessione quando app messa in background
    override fun onPause() {
        super.onPause()

        try {
            disconnect()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

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