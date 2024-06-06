package com.example.noteslaundry

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.noteslaundry.repository.LaundryNoteRepository
import com.example.noteslaundry.database.LaundryNoteDatabase
import com.example.noteslaundry.database.dao.LaundryNoteDao
import com.example.noteslaundry.database.entity.LaundryNote
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNote : AppCompatActivity() {
    private lateinit var editTextDate: EditText

    private var CelanaCounter : Int = 0
    private var JaketCounter : Int= 0
    private var BajuCounter : Int= 0
    private var SelimutCounter : Int= 0
    private lateinit var noteRepository: LaundryNoteRepository
    private lateinit var calendar: Calendar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        noteRepository = LaundryNoteRepository(
            LaundryNoteDatabase.getDatabase(this@AddNote).laundryNoteDao()
        )
        editTextDate = findViewById(R.id.editTextDate)
        calendar = Calendar.getInstance()

        // Inisialisasi komponen UI
        val textViewCelanaCounter = findViewById<TextView>(R.id.textViewCelanaCounter)
        val textViewJaketCounter = findViewById<TextView>(R.id.textViewJaketCounter)
        val textViewBajuCounter = findViewById<TextView>(R.id.textViewBajuCounter)
        val textViewSelimutCounter = findViewById<TextView>(R.id.textViewSelimutCounter)
        val buttonPlusCelana = findViewById<Button>(R.id.buttonPlusCelana)
        val buttonMinusCelana = findViewById<Button>(R.id.buttonMinusCelana)
        val buttonPlusJaket = findViewById<Button>(R.id.buttonPlusJaket)
        val buttonMinusJaket = findViewById<Button>(R.id.buttonMinusJaket)
        val buttonPlusBaju = findViewById<Button>(R.id.buttonPlusBaju)
        val buttonMinusBaju = findViewById<Button>(R.id.buttonMinusBaju)
        val buttonPlusSelimut = findViewById<Button>(R.id.buttonPlusSelimut)
        val buttonMinusSelimut = findViewById<Button>(R.id.buttonMinusSelimut)
        val buttonAddNote = findViewById<Button>(R.id.buttonAddNote)

        // Mendengarkan klik pada tombol-tombol penambahan dan pengurangan
        buttonPlusCelana.setOnClickListener {
            CelanaCounter++
            textViewCelanaCounter.text = CelanaCounter.toString()
        }

        buttonMinusCelana.setOnClickListener {
            if (CelanaCounter > 0) {
                CelanaCounter--
                textViewCelanaCounter.text = CelanaCounter.toString()
            }
        }

        buttonPlusJaket.setOnClickListener {
            JaketCounter++
            textViewJaketCounter.text = JaketCounter.toString()
        }

        buttonMinusJaket.setOnClickListener {
            if (JaketCounter > 0) {
                JaketCounter--
                textViewJaketCounter.text = JaketCounter.toString()
            }
        }

        buttonPlusBaju.setOnClickListener {
            BajuCounter++
            textViewBajuCounter.text = BajuCounter.toString()
        }

        buttonMinusBaju.setOnClickListener {
            if (BajuCounter > 0) {
                BajuCounter--
                textViewBajuCounter.text = BajuCounter.toString()
            }
        }
        buttonPlusSelimut.setOnClickListener {
            SelimutCounter++
            textViewSelimutCounter.text = SelimutCounter.toString()
        }

        buttonMinusSelimut.setOnClickListener {
            if (SelimutCounter > 0) {
                SelimutCounter--
                textViewSelimutCounter.text = SelimutCounter.toString()
            }
        }

        // Mendengarkan klik pada tombol untuk menambahkan catatan laundry
        buttonAddNote.setOnClickListener {
            val note = LaundryNote(
                tanggal = calendar.timeInMillis,
                baju = BajuCounter,
                celana = CelanaCounter,
                jaket = JaketCounter,
                selimut = SelimutCounter
            )

            lifecycleScope.launch {
                try {
                    // Panggil metode repository untuk menyimpan pesanan
                    noteRepository.insertNote(note)
                    Toast.makeText(this@AddNote, "Pesanan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    // Clear input fields after adding order
                    textViewCelanaCounter.text = 0.toString()
                    textViewBajuCounter.text = 0.toString()
                    textViewJaketCounter.text = 0.toString()
                    textViewSelimutCounter.text = 0.toString()

                    Intent(this@AddNote, MainActivity::class.java).also {
                        startActivity(it) // Memulai AddCatatanLaundry Activity
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@AddNote, "Gagal menambahkan pesanan", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mendengarkan klik pada editTextDate untuk menampilkan dialog pemilih tanggal
        editTextDate.setOnClickListener {
            showDateTimePicker()
        }
    }

    // Fungsi untuk menampilkan dialog pemilih tanggal
    // Fungsi untuk menampilkan dialog pemilih tanggal
    private fun showDateTimePicker() {
        val dateTimePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dateTimePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dateTimePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateDateTimeEditText()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateDateTimeEditText() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        editTextDate.setText(dateFormat.format(calendar.time))
    }

}