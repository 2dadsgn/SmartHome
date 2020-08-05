package com.example.smarthome.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome.Room.Arduino
import com.example.smarthome.Room.Stanza

class SharedSceltaStanza : ViewModel(){

    //nome dell'arduino room inserito nella creazione stanza
    var ArduinoRoom:String = "null"

    fun setRoom(nome:String){
        ArduinoRoom = nome
    }

    fun getRoom():String{
        return ArduinoRoom
    }

    //array per stanze disponibili per fragment Home
    var stanze = arrayOf("null","null","null")


}