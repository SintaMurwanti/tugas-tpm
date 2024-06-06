package com.example.noteslaundry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslaundry.adapter.LaundryNoteAdapter
import com.example.noteslaundry.database.LaundryNoteDatabase
import com.example.noteslaundry.database.dao.LaundryNoteDao
import com.example.noteslaundry.database.entity.LaundryNote
import com.example.noteslaundry.databinding.ActivityMainBinding
import com.example.noteslaundry.repository.LaundryNoteRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mylaundrynote.NotificationReceiver
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: LaundryNoteAdapter
    private var noteList: MutableList<LaundryNote> = mutableListOf()
    private lateinit var noteRepository: LaundryNoteRepository
    private lateinit var laundryNoteDao: LaundryNoteDao


    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private val alarmManager by lazy { getSystemService(ALARM_SERVICE) as AlarmManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLocation()

        val userLocationTextView = findViewById<TextView>(R.id.userLocationTextView)
        val btnAdd = findViewById<FloatingActionButton>(R.id.addcatatan)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 2)
        noteAdapter = LaundryNoteAdapter(noteList) { clickedNote ->
            // Melakukan operasi saat item diklik dengan mengirim objek LaundryNote ke EditNoteActivity
            val intent = Intent(this@MainActivity, EditNote::class.java)
            intent.putExtra("laundryNote", clickedNote)
            startActivity(intent)
        }

        recyclerView.adapter = noteAdapter

        val noteDao = LaundryNoteDatabase.getDatabase(this@MainActivity).laundryNoteDao()

        noteRepository = LaundryNoteRepository(noteDao)
        loadNotes()

        // Menetapkan listener onClick ke FloatingActionButton
        btnAdd.setOnClickListener{
            // Membuat intent untuk memulai AddCatatanLaundry Activity
            Intent(this, AddNote::class.java).also{
                startActivity(it) // Memulai AddCatatanLaundry Activity
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        addresses?.let { addressList ->
                            if (addressList.isNotEmpty()) {
                                val address: Address = addressList[0]
                                mainBinding.userLocationTextView.text = "Lokasi Saya: ${address.locality}"
//                                mainBinding.userLocationTextView.text = "Lokasi Saya: ${address.getAddressLine(0)}"
                            } else {
                                mainBinding.userLocationTextView.text = "Lokasi tidak ditemukan"
                            }
                        } ?: kotlin.run {
                            mainBinding.userLocationTextView.text = "Lokasi tidak ditemukan"
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun loadNotes() {
        GlobalScope.launch(Dispatchers.IO) {
            val fetchedNotes = noteRepository.getAllNotes()
            // Memperbarui UI di dalam blok withContext menggunakan Main dispatcher
            withContext(Dispatchers.Main) {
                // Memperbarui noteList dan memberitahu adapter bahwa data telah berubah
                noteList.clear() // Bersihkan noteList sebelum menambahkan data baru
                noteList.addAll(fetchedNotes) // Tambahkan semua data yang diambil dari repository ke noteList
                noteAdapter.notifyDataSetChanged()

                scheduleNotifications()
            }
        }
    }

    private fun scheduleNotifications() {
        Log.d("laundrynotess", "barumulai ${noteList.toString()}")
        for (note in noteList) {
            Log.d("laundrynotess", "mulai")
            val pickupTime = note.tanggal // Ambil waktu ambil dari catatan
            val currentTime = System.currentTimeMillis()

            // Jika waktu pengambilan sudah melewati waktu sekarang
            if (pickupTime == currentTime) {
                Log.d("laundrynotess", currentTime.toString())
                // Tampilkan notifikasi
                showNotification(note)
            } else if(pickupTime > currentTime){
                // Jadwalkan notifikasi untuk waktu ambil yang sudah disediakan
                Log.d("laundrynotess", currentTime.toString())
                scheduleNotification(note, pickupTime)
            }
        }
    }

    private fun scheduleNotification(note: LaundryNote, pickupTime: Long) {
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.putExtra("noteId", note.id)
        val pendingIntent = PendingIntent.getBroadcast(
            this, note.id, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Jadwalkan notifikasi menggunakan AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            pickupTime,
            pendingIntent
        )
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(note: LaundryNote) {
        val channelId = "LaundryNoteChannel" // ID saluran notifikasi yang ingin Anda gunakan
        val notificationId = note.id

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_speaker_notes_24)
            .setContentTitle("Laundry Pickup")
            .setContentText("Your laundry pickup is scheduled now.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        // Check if the notification channel exists, if not, create it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            if (channel == null) {
                val newChannel = NotificationChannel(
                    channelId,
                    "Laundry Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Channel for laundry notifications"
                }
                notificationManager.createNotificationChannel(newChannel)
            }
        }

        // Show the notification
        notificationManager.notify(notificationId, builder.build())
    }
}

//class NotificationReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val noteId = intent.getIntExtra("noteId", 0)
//        // Handle notification here if needed
//    }
//}
