package com.example.smarthome.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome.Room.Arduino
import com.example.smarthome.Room.Stanza

class viewmodel : ViewModel() {


    //cursore per arduino tabella
    var ArduinoCursore: List<Arduino>? = null


    //cursore per tabella stanza
    var StanzaCursore: List<Stanza>? = null

    //arduino unicode inserito nel creazione stanza ed arduino
    var ArduinoCode: String = "null"

    fun setArduinocode(code: String) {
        ArduinoCode = code
    }

    fun getarduinoCode(): String {
        return ArduinoCode
    }


    //nome dell'arduino room inserito nella creazione stanza
    var ArduinoRoom: String = "null"

    fun setarduinoRoom(nome: String) {
        ArduinoRoom = nome
    }

    fun getarduinoRoom(): String {
        return ArduinoRoom
    }

    //array per stanze disponibili per fragment Home
    var stanze = arrayOf("null", "null", "null")

    val arduinoCodeMutable: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


}