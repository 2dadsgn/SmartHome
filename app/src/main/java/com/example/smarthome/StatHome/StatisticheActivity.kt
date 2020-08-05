package com.example.smarthome.StatHome

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smarthome.ActivityStanze.HomeStanzeActivity
import com.example.smarthome.ActivityStanze.fragHomeStanze
import com.example.smarthome.R
import com.example.smarthome.createNewArduino.newArduinoActivty

class StatisticheActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistiche)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        intent = Intent(this, HomeStanzeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }



}