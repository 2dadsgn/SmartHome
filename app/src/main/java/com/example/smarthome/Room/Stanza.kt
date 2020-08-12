package com.example.smarthome.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = Arduino::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("ArduinoCode"),
    onDelete = ForeignKey.CASCADE)
))
data class Stanza(
    @PrimaryKey val nome: String,
    @ColumnInfo val ArduinoCode: String,
    @ColumnInfo val LastData: String?
)
