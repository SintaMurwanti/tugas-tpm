package com.example.noteslaundry.repository

import com.example.noteslaundry.database.dao.LaundryNoteDao
import com.example.noteslaundry.database.entity.LaundryNote

class LaundryNoteRepository(private val laundryNoteDao : LaundryNoteDao) {
    suspend fun getAllNotes(): List<LaundryNote>{
        return laundryNoteDao.getAll()
    }

    suspend fun insertNote(note: LaundryNote){
        laundryNoteDao.insertNote(note)
    }

    suspend fun updateNote(note: LaundryNote){
        laundryNoteDao.updateNote(note)
    }

    suspend fun deleteNote(note: LaundryNote){
        laundryNoteDao.deleteNote(note)
    }
}