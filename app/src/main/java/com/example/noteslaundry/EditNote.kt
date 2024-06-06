package com.example.noteslaundry

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.noteslaundry.database.LaundryNoteDatabase
import com.example.noteslaundry.database.entity.LaundryNote
import com.example.noteslaundry.repository.LaundryNoteRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditNote : AppCompatActivity() {
    private lateinit var editTextDateEdit: EditText

    private var CelanaCounterEdit: Int = 0
    private var JaketCounterEdit: Int = 0
    private var BajuCounterEdit: Int = 0
    private var SelimutCounterEdit: Int = 0
    private lateinit var noteRepository: LaundryNoteRepository
    private lateinit var calendar: Calendar
    private var laundryNote: LaundryNote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        noteRepository = LaundryNoteRepository(
            LaundryNoteDatabase.getDatabase(this@EditNote).laundryNoteDao()
        )
        editTextDateEdit = findViewById(R.id.editTextDateEdit)
        calendar = Calendar.getInstance()
        findViewById<Button>(R.id.buttonDeleteNote).setOnClickListener {
            deleteNote()
        }
        // Inisialisasi komponen UI
        val textViewCelanaCounterEdit = findViewById<TextView>(R.id.textViewCelanaCounterEdit)
        val textViewJaketCounterEdit = findViewById<TextView>(R.id.textViewJaketCounterEdit)
        val textViewBajuCounterEdit = findViewById<TextView>(R.id.textViewBajuCounterEdit)
        val textViewSelimutCounterEdit = findViewById<TextView>(R.id.textViewSelimutCounterEdit)
        val buttonPlusCelanaEdit = findViewById<Button>(R.id.buttonPlusCelanaEdit)
        val buttonMinusCelanaEdit = findViewById<Button>(R.id.buttonMinusCelanaEdit)
        val buttonPlusJaketEdit = findViewById<Button>(R.id.buttonPlusJaketEdit)
        val buttonMinusJaketEdit = findViewById<Button>(R.id.buttonMinusJaketEdit)
        val buttonPlusBajuEdit = findViewById<Button>(R.id.buttonPlusBajuEdit)
        val buttonMinusBajuEdit = findViewById<Button>(R.id.buttonMinusBajuEdit)
        val buttonPlusSelimutEdit = findViewById<Button>(R.id.buttonPlusSelimutEdit)
        val buttonMinusSelimutEdit = findViewById<Button>(R.id.buttonMinusSelimutEdit)
        val buttonEditNote = findViewById<Button>(R.id.buttonEditNote)

        // Mendapatkan objek LaundryNote dari intent
        laundryNote = intent.getParcelableExtra("laundryNote")
        Log.d("testt", laundryNote.toString())
        textViewCelanaCounterEdit.text = laundryNote?.celana.toString()
        textViewBajuCounterEdit.text = laundryNote?.baju.toString()
        textViewJaketCounterEdit.text = laundryNote?.jaket.toString()
        textViewSelimutCounterEdit.text = laundryNote?.selimut.toString()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        editTextDateEdit.setText(dateFormat.format(laundryNote?.tanggal))

        CelanaCounterEdit = laundryNote?.celana ?: 0
        JaketCounterEdit = laundryNote?.jaket ?: 0
        BajuCounterEdit = laundryNote?.baju ?: 0
        SelimutCounterEdit = laundryNote?.selimut ?: 0
        // Mendengarkan klik pada tombol-tombol penambahan dan pengurangan
        buttonPlusCelanaEdit.setOnClickListener {
            CelanaCounterEdit++
            textViewCelanaCounterEdit.text = CelanaCounterEdit.toString()
        }

        buttonMinusCelanaEdit.setOnClickListener {
            if (CelanaCounterEdit > 0) {
                CelanaCounterEdit--
                textViewCelanaCounterEdit.text = CelanaCounterEdit.toString()
            }
        }

        buttonPlusJaketEdit.setOnClickListener {
            JaketCounterEdit++
            textViewJaketCounterEdit.text = JaketCounterEdit.toString()
        }

        buttonMinusJaketEdit.setOnClickListener {
            if (JaketCounterEdit > 0) {
                JaketCounterEdit--
                textViewJaketCounterEdit.text = JaketCounterEdit.toString()
            }
        }

        buttonPlusBajuEdit.setOnClickListener {
            BajuCounterEdit++
            textViewBajuCounterEdit.text = BajuCounterEdit.toString()
        }

        buttonMinusBajuEdit.setOnClickListener {
            if (BajuCounterEdit > 0) {
                BajuCounterEdit--
                textViewBajuCounterEdit.text = BajuCounterEdit.toString()
            }
        }
        buttonPlusSelimutEdit.setOnClickListener {
            SelimutCounterEdit++
            textViewSelimutCounterEdit.text = SelimutCounterEdit.toString()
        }

        buttonMinusSelimutEdit.setOnClickListener {
            if (SelimutCounterEdit > 0) {
                SelimutCounterEdit--
                textViewSelimutCounterEdit.text = SelimutCounterEdit.toString()
            }
        }

        // Mendengarkan klik pada tombol untuk menambahkan catatan laundry
        buttonEditNote.setOnClickListener {
            val note = LaundryNote(
                id = laundryNote?.id ?: 0, // Menggunakan id dari note yang sedang di-edit
                tanggal = calendar.timeInMillis,
                baju = BajuCounterEdit,
                celana = CelanaCounterEdit,
                jaket = JaketCounterEdit,
                selimut = SelimutCounterEdit
            )

            lifecycleScope.launch {
                try {
                    // Panggil metode repository untuk menyimpan pesanan
                    noteRepository.updateNote(note)
                    Toast.makeText(this@EditNote, "Catatan berhasil diubah", Toast.LENGTH_SHORT).show()
                    // Clear input fields after adding order
                    textViewCelanaCounterEdit.text = 0.toString()
                    textViewBajuCounterEdit.text = 0.toString()
                    textViewJaketCounterEdit.text = 0.toString()
                    textViewSelimutCounterEdit.text = 0.toString()

                    // Kembali ke MainActivity setelah berhasil menambah catatan
                    Intent(this@EditNote, MainActivity::class.java).also {
                        startActivity(it)
                        finish() // Menutup activity EditNote
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@EditNote, "Gagal mengedit", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mendengarkan klik pada editTextDate untuk menampilkan dialog pemilih tanggal
        editTextDateEdit.setOnClickListener {
            showDateTimePicker()
        }
    }

    private fun deleteNote() {
        lifecycleScope.launch {
            try {
                // Panggil metode repository untuk menghapus catatan
                laundryNote?.let { noteRepository.deleteNote(it) }
                Toast.makeText(this@EditNote, "Catatan berhasil dihapus", Toast.LENGTH_SHORT).show()
                Intent(this@EditNote, MainActivity::class.java).also {
                    startActivity(it)
                    finish() // Menutup activity EditNote setelah penghapusan berhasil
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditNote, "Gagal menghapus catatan", Toast.LENGTH_SHORT).show()
            }
        }
    }
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
        editTextDateEdit.setText(dateFormat.format(calendar.time))
    }
}
