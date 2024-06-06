package com.example.noteslaundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslaundry.database.entity.LaundryNote
import com.example.noteslaundry.R
import java.text.SimpleDateFormat
import java.util.*

class LaundryNoteAdapter(
    private val noteList: List<LaundryNote>,
    private val onItemClick: (LaundryNote) -> Unit // Listener untuk klik pada item
) : RecyclerView.Adapter<LaundryNoteAdapter.OrderViewHolder>() {
    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggal: TextView = itemView.findViewById(R.id.tanggal)
        val baju: TextView = itemView.findViewById(R.id.baju)
        val celana: TextView = itemView.findViewById(R.id.celana)
        val jaket: TextView = itemView.findViewById(R.id.jaket)
        val selimut: TextView = itemView.findViewById(R.id.selimut)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentItem = noteList[position]

        // Mengubah millis menjadi format tanggal dan waktu
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(currentItem.tanggal))

        // Menetapkan teks pada TextView
        holder.tanggal.text = "Tanggal Ambil: $formattedDate"
        holder.baju.text = "1. Baju ${currentItem.baju} buah"
        holder.celana.text = "2. Celana ${currentItem.celana} buah"
        holder.jaket.text = "3. Jaket ${currentItem.jaket} buah"
        holder.selimut.text = "4. Selimut ${currentItem.selimut} buah"

        holder.itemView.setOnClickListener {
            onItemClick(currentItem) // Mengirim currentItem langsung saat item diklik
        }
    }
}
