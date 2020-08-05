package com.example.smarthome


import android.app.DownloadManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.smarthome.ActivityStanze.HomeStanzeActivity
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.Room.Arduino
import com.example.smarthome.Room.Stanza
import com.example.smarthome.createNewArduino.fragInsArduino
import com.example.smarthome.createNewArduino.newArduinoActivty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlin.Exception


val TAG: String = "MainAct"

class MainActivity : AppCompatActivity() {


    private var ArduinoCursore:List<Arduino>? = null
    private var StanzaCursore:List<Stanza>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        //oggetto DB
        val db = AppClassDatabase.get(application)

        setContentView(R.layout.loading_home)


        try {
            CoroutineScope(IO).launch {
                //crea i cursori all'avvio
                //Todo deve reperire data dal DB e inserirlo nel view model
                CoroutineScope(Main).launch {
                    ArduinoCursore = db.arduinoDao().getAll()
                    StanzaCursore = db.stanzaDao().getAll()
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        Handler().postDelayed({

            //controlla se ci esistono stanze
            try {
                if (ArduinoCursore.isNullOrEmpty()) {
                    intent = Intent(this, HomeStanzeActivity::class.java)
                    intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)

                    startActivity(intent)

                    Log.d(TAG, "CODE 1 ---no arduino FOUND! transizione to fragInsArduino---")

                } else {

                    //start home activity
                    intent = Intent(this, HomeStanzeActivity::class.java)
                    intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)

                    startActivity(intent)
                    Log.d(
                        TAG,
                        "Arduino code found in DB, launching HomeStat activity "
                    )
                }
            } catch (e: Exception) {
                Log.d(TAG, e.toString())

            }
            //imposta la schermata principale con view stanze
            //per evitare di avere la schermata di caricamente prima di uscire dall app
            //todo sistemare e impostare chiusura app su backButton
            setContentView(R.layout.activity_home_stanze)
            val FragHome = fragInsArduino()
            supportFragmentManager.beginTransaction().add(R.id.fragmentHolderHome, FragHome).commit()

        }, 2000)
    }
}
