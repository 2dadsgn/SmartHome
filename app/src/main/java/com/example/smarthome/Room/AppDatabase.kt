package com.example.smarthome.Room

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Arduino::class,Stanza::class,Data::class), version = 1, exportSchema = false)
public abstract class AppClassDatabase : RoomDatabase() {

    abstract fun dataDao(): DataDao
    abstract fun stanzaDao(): StanzaDao
    abstract fun arduinoDao(): ArduinoDao

    companion object {
        fun get(application: Application) : AppClassDatabase{
            return Room.databaseBuilder(application,AppClassDatabase::class.java,"database-smartRoom")
            .build()
        }
    }
}