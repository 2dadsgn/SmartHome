package com.example.smarthome.ActivityStanze

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.test.runner.intent.IntentMonitor
import com.example.smarthome.R
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.SearchArduinoBT.SearchArduinoActivity
import com.example.smarthome.StatHome.StatisticheActivity
import com.example.smarthome.createNewArduino.newArduinoActivty
import com.example.smarthome.viewModel.HomeStatModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

val TAG = "MainAct"

class HomeStanzeActivity : AppCompatActivity(),
    fragHomeStanze.onAddRoomPressedListener, fragHomeStanze.onRoomSelectedListener {

    companion object{
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    }

    //dichiarazione ViewModel
    val model: HomeStatModel by viewModels()

    //fragment manager
    val FragManager = supportFragmentManager.beginTransaction()

    //fragment home
    val FragHome = fragHomeStanze()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_stanze)
        FragManager.add(R.id.fragmentHolderHome, FragHome)
        FragManager.addToBackStack(null)
        FragManager.commit()


        //lancia coroutine per pescare dati da DB
        CoroutineScope(IO).launch {
            val db = AppClassDatabase.get(application)
            model.Stanze = db.stanzaDao().getAll()
            model.arduini = db.arduinoDao().getAll()
            Log.d(com.example.smarthome.TAG, "model.Stanze!!.indices.toString()")
        }
    }

    override fun onStart() {
        super.onStart()
        //crea adattatore bluetooth e richede attivazione
        if (bluetoothAdapter == null) {
            Log.d(com.example.smarthome.TAG,"bluetooth non available")
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 2)
            Log.d(com.example.smarthome.TAG,"bluetooth !!")
        }


        try {
            //ottiene i devide paired
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

            if (model.arduini == null){
                //non fare nulla
            }
            else {
                pairedDevices?.forEach { device ->

                    //ottiene info dispositivi paired
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address

                    //itera gli arduini per verificare chi è già accoppiato col dispositivo mobile
                    //todo si presume che arduini abbiano come nome il codice arduino
                    for (i in model.arduini!!){
                        if(deviceName == i.id){
                            i.paired = 1
                        }
                    }
                }

                //ricerca arduini non ancora associati
                for(i in model.arduini!!){
                    //se non è stato ancora associato
                    if(i.paired != 1){
                        //todo va associato
                        //todo lancio una coroutine per cercare dispositivi nelle vicinanze con nomi corrisspondenti
                        //ToDo appena trova un arduino effettua connessione e retrieve data
                        Log.d(TAG,"c'è qualcuno da associare")
                        var intent = Intent(this,SearchArduinoActivity::class.java)
                        //startActivityForResult(intent,2)
                    }
                }
            }

        }
        catch (e : Exception){
            Log.d(TAG,e.toString())
        }


    }



    override fun onAddRoomPressed() {
        finish()
        intent = Intent(this, newArduinoActivty::class.java)
        startActivity(intent)
    }

    //inserisce listener alla creazione dell'attività
    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is fragHomeStanze) {
            Log.d(com.example.smarthome.TAG, "fragsceltaArduino attached and listener placed")
            fragment.setOnAddRoomPressedListener(this)
            fragment.setOnRoomSelectedListener(this)
        }
    }
    override fun onRoomSelected() {
        finish()
        intent = Intent(this,StatisticheActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivityForResult(intent,1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}