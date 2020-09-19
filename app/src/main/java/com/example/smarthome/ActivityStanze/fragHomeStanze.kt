package com.example.smarthome.ActivityStanze

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.smarthome.R
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.Room.Arduino
import com.example.smarthome.Room.Stanza
import com.example.smarthome.StatHome.StatisticheActivity
import com.example.smarthome.TAG
import com.example.smarthome.viewModel.HomeStatModel
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.log




class fragHomeStanze : Fragment() {

    //dichiarazione ViewModel
    val model: HomeStatModel by activityViewModels()

    //UUID
    var MyUUID = "123abcd"


    internal var callback: onAddRoomPressedListener? = null

    internal var callback2: onRoomSelectedListener? = null

    internal var callbackDelete: onDeletePressedListener? = null


    fun setOnAddRoomPressedListener(callback: onAddRoomPressedListener) {
        this.callback = callback
    }


    fun setOnRoomSelectedListener(callback: onRoomSelectedListener) {
        this.callback2 = callback
    }

    fun setDeletePressedListener(callback: onDeletePressedListener) {
        this.callbackDelete = callback
    }


    //interfaccia per callback su click della stanza
    interface onRoomSelectedListener{
        fun onRoomSelected(nome:String)
    }

    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface onAddRoomPressedListener {
        fun onAddRoomPressed()
    }

    interface onDeletePressedListener {
        fun onDeletePressed(nomeStanza:String)
    }


    lateinit var db  : AppClassDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val applicazione:Application? = activity?.application
        db =  AppClassDatabase.get(applicazione!!)

        //ricerca in lista dispositivi associati  e imposta valore paired se associati
        try {
            //ottiene i device paired
            val pairedDevices: Set<BluetoothDevice>? = HomeStanzeActivity.bluetoothAdapter?.bondedDevices

            pairedDevices?.forEach { device ->

                //ottiene info dispositivi paired
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                var listaArduini : List<Arduino>

                CoroutineScope(Dispatchers.Default).launch {
                    listaArduini = db.arduinoDao().getAll()

                    if (listaArduini.size != 0){

                        //itera gli arduini per verificare chi è già accoppiato col dispositivo mobile
                        Log.d(TAG,"--Lista device associati--")
                        for (i in listaArduini!!) {
                            Log.d(TAG, "    ${deviceName} >--<  ${i.id}  ")
                            if (deviceName == i.id) {
                                Log.d(TAG, "    the above arduino is already paired!")
                                i.paired = 1

                                //inserisce indirizzo in DB
                                i.address = deviceHardwareAddress

                                //aggiorna dati ardunio inserendo PAIRED e ADDRESS in DB
                                CoroutineScope(Dispatchers.Default).launch {
                                    db.arduinoDao().update(i)
                                }
                            }
                        }
                        Log.d(TAG,"---Fine lista---")
                    }
                }
            }
        }
        catch (e : Exception){
            Log.d(TAG,"Errore in ricerca dispositivi accoppiato in FragHome fragment da HomestanzeActivity" + e.toString())
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //lista di ADDROOMTEXT
        val AddRoomStanze = mutableListOf(addroomStanza2,addroomStanza1,addroomStanza3)

        //lista di delete
        val DeleteButtons = mutableListOf(delete2,delete1,delete3)

        //lista colori
        val listaColori = mutableListOf("#F2B6CC","#BAD0FF","#FFC4B1")

        //lista di Framelayout
        val FrameLayoutList = mutableListOf(stanza2,stanza1,stanza3)


        //lista di TextViewStanza
        val TextViewStanze = mutableListOf(stanza2Textview,stanza1Textview,stanza3Textview)

        //lista di BTConnectionButton
        val BTButtons = mutableListOf(BTConnection2,BTConnection1,BTConnection3)

        //lista container stanze
        val StanzeContainer = mutableListOf(stanza2,stanza1,stanza3)



        CoroutineScope(Dispatchers.Default).launch {
            var listaStanza : MutableList<Stanza> = db.stanzaDao().getAll().toMutableList()

            var indice = listaStanza.indices

            //effettua controllo su size lista , se non ci sono stanze non c'è niente da caricare

            if (listaStanza.size != 0) {

                for ( i in indice!!){



                    //ricerca prima arduino tramite stanza e poi il valore paired
                    var code = db.stanzaDao().findByName(model.Stanze?.get(i)?.nome.toString()).ArduinoCode
                    var paired = db.arduinoDao().getOneWithID(code).paired

                    //imposta icona bluetooth se arduino associato
                    if (paired == 1){
                        BTButtons.get(i).setBackgroundResource(android.R.drawable.stat_sys_data_bluetooth)

                        //imposta il colore del riquadro stanza
                        FrameLayoutList.get(i) //todo .....
                    }

                    //nasconde la scritta addRoom se esistente
                    AddRoomStanze.get(i).isVisible = false

                    //mostra tasto per eliminazione
                    DeleteButtons.get(i).isVisible = true

                    //imposta nome stanza nel TextView
                    TextViewStanze.get(i).setText(model.Stanze?.get(i)?.nome.toString())

                    //imposta il listener
                    TextViewStanze.get(i).setOnClickListener{
                        callback2?.onRoomSelected(TextViewStanze.get(i).text.toString())
                        HomeStanzeActivity.ActualStanza = TextViewStanze.get(i).text.toString()
                    }
                    StanzeContainer.get(i).setOnClickListener{
                        callback2?.onRoomSelected(TextViewStanze.get(i).text.toString())
                        HomeStanzeActivity.ActualStanza = TextViewStanze.get(i).text.toString()
                    }
                }
            }
            else{
                Log.d(TAG,"Attenzione nessuna stanza da caricare")
            }
        }




        addroomStanza1.setOnClickListener {
            callback?.onAddRoomPressed()
        }
        addroomStanza2.setOnClickListener {
            callback?.onAddRoomPressed()
        }
        addroomStanza3.setOnClickListener {
            callback?.onAddRoomPressed()
        }

        delete1.setOnClickListener{
            callbackDelete?.onDeletePressed(stanza1Textview.text.toString())
        }
        delete2.setOnClickListener{
            callbackDelete?.onDeletePressed(stanza2Textview.text.toString())
        }
        delete3.setOnClickListener{
            callbackDelete?.onDeletePressed(stanza3Textview.text.toString())
        }

    }

}