package com.example.smarthome.viewModel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.smarthome.Room.AppClassDatabase
import com.example.smarthome.Room.Arduino
import com.example.smarthome.Room.Stanza

class HomeStatModel: ViewModel(){

    //lista oggetti stanza da Db
    var Stanze: MutableList<Stanza> ? = null

     var arduini : MutableList<Arduino> ? = null



    

}