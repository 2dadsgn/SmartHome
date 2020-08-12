package com.example.smarthome.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Arduino(
    @PrimaryKey var id: String,
    var paired : Int = 0,
    var address : String? = null

)
