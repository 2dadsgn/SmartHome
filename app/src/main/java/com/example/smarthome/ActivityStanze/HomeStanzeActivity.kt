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
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.test.runner.intent.IntentMonitor
import com.example.smarthome.R
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.SearchArduinoBT.SearchArduinoActivity
import com.example.smarthome.StatHome.StatisticheActivity
import com.example.smarthome.createNewArduino.newArduinoActivty
import com.example.smarthome.viewModel.HomeStatModel
import kotlinx.android.synthetic.main.activity_home_stanze.*
import kotlinx.android.synthetic.main.activity_statistiche.view.*
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

val TAG = "MainAct"

class HomeStanzeActivity : AppCompatActivity(),
    fragHomeStanze.onAddRoomPressedListener, fragHomeStanze.onRoomSelectedListener,fragHomeStanze.onDeletePressedListener {


    companion object{
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        var ActualStanza :String ? = null
    }


    //dichiarazione ViewModel
    val model: HomeStatModel by viewModels()

    //fragment manager
    val FragManager = supportFragmentManager.beginTransaction()

    //fragment home
    val FragHome = fragHomeStanze()

    lateinit var db :AppClassDatabase

    var url:String? = "http://google.com"

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {

        //Database
         db = AppClassDatabase.get(application)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_stanze)
        FragManager.add(R.id.fragmentHolderHome, FragHome)
        FragManager.addToBackStack(null)
        FragManager.commit()

        /*apertura menu laterale su click su menu*/
        menu2.setOnClickListener {
            //per poter aprire il menu
            val params: ViewGroup.LayoutParams = menu_laterale.layoutParams
            params.width = 500
           



        }

        aggiornaListaModel()
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


    override fun onDeletePressed(nomestanza:String) {

        try {
            //effettua cancellazione stanza ed arduino
            CoroutineScope(Dispatchers.Default).launch {
                Log.d(TAG,nomestanza)
                var stanza = db.stanzaDao().findByName(nomestanza)

                var arduino = db.arduinoDao().getOneWithID(stanza.ArduinoCode)

                db.stanzaDao().delete(stanza)
                db.arduinoDao().delete(arduino)
            }
        }
        catch (ex : Exception){
            Log.d(TAG,ex.toString())
        }

        //rilancia la activity per aggiornare UI
        finish()
        intent = Intent(this,HomeStanzeActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
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
            fragment.setDeletePressedListener(this)
        }
    }


    override fun onRoomSelected(nome:String) {

        finish()
        intent = Intent(this,StatisticheActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivityForResult(intent,1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    //funzione per aggiornare lista stanze ed arduino in viewmodel
     fun aggiornaListaModel(){
         //lancia coroutine per pescare dati da DB, inserirli nel viewmodel
         //cosi che il fragment fraghomestanza possa avere dati da visuallizare
         CoroutineScope(IO).launch {
             val db = AppClassDatabase.get(application)
             model.Stanze?.clear()
             model.arduini?.clear()
             model.Stanze = db.stanzaDao().getAll().toMutableList()
             model.arduini = db.arduinoDao().getAll().toMutableList()
             try {
                 Log.d(TAG,"---------******----------")
                 Log.d(TAG,db.stanzaDao().getAll().toString())
             }
             catch (ex : Exception){
                 Log.d(TAG,ex.toString())
             }

         }
     }
}