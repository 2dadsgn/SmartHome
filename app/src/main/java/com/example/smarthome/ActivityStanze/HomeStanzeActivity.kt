package com.example.smarthome.ActivityStanze

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.test.runner.intent.IntentMonitor
import com.example.smarthome.R
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.SearchArduinoBT.SearchArduinoActivity
import com.example.smarthome.StatHome.StatisticheActivity
import com.example.smarthome.createNewArduino.newArduinoActivty
import com.example.smarthome.viewModel.HomeStatModel
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    lateinit var db :AppClassDatabase

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {

        //Database
         db = AppClassDatabase.get(application)

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
            Log.d(com.example.smarthome.TAG,"richiesta di attivazione per utente")
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