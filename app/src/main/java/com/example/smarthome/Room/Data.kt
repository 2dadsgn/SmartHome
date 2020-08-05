package com.example.smarthome.Room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*


@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = Arduino::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("ArduinoCode"),
    onDelete = ForeignKey.CASCADE)
))
data class Data(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val temperature: Int,
    val ArduinoCode: String,
    val Time: String
)