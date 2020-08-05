package com.example.smarthome.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ArduinoDao {
    @Query("SELECT * FROM Arduino")
    suspend fun getAll(): List<Arduino>

    @Query("SELECT * FROM Arduino WHERE id = :ArduinoId" )
    suspend fun getOneWithID(ArduinoId: String):Arduino

    @Insert
    suspend fun insertAll(vararg users: Arduino)

    @Delete
    fun delete(user: Arduino)
}
