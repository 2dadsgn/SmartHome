package com.example.smarthome.createNewArduino

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.smarthome.ActivityStanze.HomeStanzeActivity
import com.example.smarthome.R
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.Room.Arduino
import com.example.smarthome.Room.Stanza
import com.example.smarthome.viewModel.viewmodel
import kotlinx.android.synthetic.main.fragment_frag_ins_arduino.*
import kotlinx.android.synthetic.main.fragment_scelta_stanza.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class newArduinoActivty : AppCompatActivity(),
    fragInsArduino.OnNextPressedListener,
    SceltaStanza.OnStanzaSceltaListener {

    //fragment manager
    val FragManager = supportFragmentManager.beginTransaction()

    //crea oggetti fragment
    val fragInsArduino = fragInsArduino()
    val fragSceltaStanza = SceltaStanza()
    val fragHome = com.example.smarthome.ActivityStanze.fragHomeStanze()

    //dichiarazione ViewModel
    val model: viewmodel by viewModels()

    //inserisce listener alla creazione dell'attività
    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is fragInsArduino) {
            Log.d(com.example.smarthome.TAG, "fragsceltaarduino attached and listener placed")
            fragment.setOnNextPressedListener(this)
        } else if (fragment is SceltaStanza) {
            Log.d(com.example.smarthome.TAG, "fragsceltastanza attached and listener placed")
            fragment.setOnstanzaSceltaListener(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_arduino)
        FragManager.add(R.id.fragmentHolderHome, fragInsArduino).commit()
    }

    //funzione per transizione fragment
    private fun transitionTofragment(frag: Fragment) {

        var manager = supportFragmentManager.beginTransaction()
        manager.replace(R.id.fragmentHolderHome, frag)
        manager.addToBackStack(null)
        manager.commit()
        Log.d(com.example.smarthome.TAG, "transizione effettuata da ...  a ${frag.toString()}")


    }


    //implementazione interfaccia di fragment Scelta stanza
    override fun onStanzaScelta() {

        //creazione  DB
        val db = AppClassDatabase.get(application)

        try {
            //inserisce  in database
            CoroutineScope(Dispatchers.Main).launch {
                //crea oggetto stanza
                val stanza = Stanza(model.getarduinoRoom(), model.getarduinoCode(), null)

                //ricerca se stanza già esiste
                val StanzaEsistente = db.stanzaDao().findByName(model.ArduinoRoom)

                if (StanzaEsistente == null) {
                    //inserisce in DB
                    db.stanzaDao().insertAll(stanza)

                    //termina activity di inserimento arduino e stanza
                    finish()

                    //transizione al nuovo frag delle stanze
                    intent = Intent(applicationContext, HomeStanzeActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d(com.example.smarthome.TAG, "stanza già presente")
                    //lancia errore per arduino già presente
                    val toast = Toast.makeText(applicationContext,"Errore stanza già esistente",Toast.LENGTH_LONG)
                    toast.show()
                }

            }
        } catch (e: Exception) {
            Log.d(com.example.smarthome.TAG, e.toString())
        }
    }


    //implementazione interfaccia di fragment ins Arduino su tasto NEXT
    override fun onNextPressed() {

        //viewmodel
        model.setArduinocode(ediTextArduinoCode.text.toString())

        //creazione  DB
        val db = AppClassDatabase.get(application)

        try {
            CoroutineScope(Dispatchers.Main).launch {
                //cerca se arduino già esistente
                val result = db.arduinoDao().getOneWithID(ediTextArduinoCode.text.toString())

                if (result == null && ediTextArduinoCode.text.toString() != "") {

                    //crea oggetto arduino e inserisce in DB
                    val arduino = Arduino(ediTextArduinoCode.text.toString())
                    db.arduinoDao().insertAll(arduino)

                    //imposta codice arduino da mostrare in frag successiva
                    model.ArduinoCode = ediTextArduinoCode.text.toString()

                    //transìzione a scelta stanza
                    transitionTofragment(fragSceltaStanza)
                }
                else if(ediTextArduinoCode.text.toString() == ""){
                    Log.d(com.example.smarthome.TAG, "arduino già presente")
                    //lancia errore per arduino non inserito
                    val toast = Toast.makeText(applicationContext,"Errore nessuno codice inserito",Toast.LENGTH_LONG)
                    toast.show()

                }
                else {

                    Log.d(com.example.smarthome.TAG, "arduino già presente")
                    //lancia errore per arduino già presente
                    val toast = Toast.makeText(applicationContext,"Errore ${ediTextArduinoCode.text.toString()} già esistente",Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        } catch (e: Exception) {
            Log.d(com.example.smarthome.TAG, e.toString())
        }
        Log.d(com.example.smarthome.TAG, "next pressed")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        intent = Intent(this, HomeStanzeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        startActivity(intent)

        Log.d(com.example.smarthome.TAG, "Premuto backButton ritorno a schermata stanze")
    }
}