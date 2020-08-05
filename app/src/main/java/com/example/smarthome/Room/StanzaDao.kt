package com.example.smarthome.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StanzaDao {
    @Query("SELECT * FROM Stanza")
    suspend fun getAll(): List<Stanza>


    @Query("SELECT * FROM Stanza WHERE Nome = :nome ")
    suspend fun findByName(nome: String): Stanza

    @Query("SELECT * FROM Stanza WHERE ArduinoCode = :code ")
    suspend fun findByArduinoCode(code: String): Stanza

    @Insert
    suspend fun insertAll(vararg users: Stanza)

    @Delete
    suspend fun delete(user: Stanza)
}
