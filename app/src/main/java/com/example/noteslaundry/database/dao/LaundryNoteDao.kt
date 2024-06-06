package com.example.noteslaundry.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.noteslaundry.database.entity.LaundryNote

@Dao
interface LaundryNoteDao {
    @Insert
    suspend fun insertNote(note: LaundryNote)

    @Query("SELECT * FROM laundrynote")
    suspend fun getAll(): List<LaundryNote>

    @Query("SELECT * FROM laundrynote WHERE id = :id")
    suspend fun getNoteById(id: Int): LaundryNote?

    @Update
    suspend fun updateNote(note: LaundryNote)

    @Delete
    suspend fun deleteNote(note: LaundryNote)
}