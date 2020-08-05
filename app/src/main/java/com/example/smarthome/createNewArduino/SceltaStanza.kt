package com.example.smarthome.createNewArduino

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.smarthome.R
import com.example.smarthome.viewModel.viewmodel
import kotlinx.android.synthetic.main.fragment_scelta_stanza.*




class SceltaStanza : Fragment() {

    //dichiarazione viewmodel
    val model: viewmodel by activityViewModels()


    //callback per sceltastanza
    internal var callback: OnStanzaSceltaListener? = null

    //listener
    fun setOnstanzaSceltaListener(callback: OnStanzaSceltaListener) {
        this.callback = callback
    }

    //interfaccia da implementare in activity
    interface OnStanzaSceltaListener {
        fun onStanzaScelta()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scelta_stanza, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //click listener per tasto kitchen  per scelta stanza e ins in DB
        Kitchen.setOnClickListener {
            model.setarduinoRoom("kitchen")
            Log.d("MainAct","Kitchen Selected!")
            callback?.onStanzaScelta()
        }
        //click listener per tasto livingroom  per scelta stanza e ins in DB
        LivingRoom.setOnClickListener {
            model.setarduinoRoom("Living Room")
            Log.d("MainAct","Livingroom selected!")
            callback?.onStanzaScelta()
        }
        //click listener per tasto bedroom per scelta stanza e ins in DB
        BedRoom.setOnClickListener {
            model.setarduinoRoom("Bed Room")
            Log.d("MainAct","Bedroom selected!")
            callback?.onStanzaScelta()
        }
    }




}