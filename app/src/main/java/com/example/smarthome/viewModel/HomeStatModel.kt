package com.example.smarthome.viewModel

import androidx.lifecycle.ViewModel
import com.example.smarthome.Room.Arduino
import com.example.smarthome.Room.Stanza

class HomeStatModel: ViewModel(){

    //lista oggetti stanza da Db
    var Stanze: List<Stanza>? = null

    var arduini : List<Arduino> ? = null

}