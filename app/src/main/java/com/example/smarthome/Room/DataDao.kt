package com.example.smarthome.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.*


@Dao
interface DataDao {
    @Query("SELECT * FROM data")
    suspend fun getAll(): List<Data>

    @Query("SELECT * FROM Data WHERE Time = :time")
    suspend fun loadAllByTime(time: String): List<Data>

    @Query("SELECT * FROM Data WHERE ArduinoCode = :code " )
    suspend fun findByArduinoCode(code:String): Data

    @Insert
    suspend fun insertAll(vararg data: Data)

    @Delete
    suspend fun delete(data: Data)
}
