package com.example.noteslaundry.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.noteslaundry.database.dao.LaundryNoteDao
import com.example.noteslaundry.database.entity.LaundryNote

@Database(entities = [LaundryNote::class], version = 1)
abstract class LaundryNoteDatabase : RoomDatabase() {
    abstract fun laundryNoteDao(): LaundryNoteDao

    companion object {
        @Volatile
        private var INSTANCE: LaundryNoteDatabase? = null

        fun getDatabase(context: Context): LaundryNoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LaundryNoteDatabase::class.java,
                    "laundry_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()// Contoh menggunakan metode migrasi
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}