package com.example.noteslaundry.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LaundryNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "tanggal") var tanggal: Long,
    @ColumnInfo(name = "baju") val baju: Int,
    @ColumnInfo(name = "celana") val celana: Int,
    @ColumnInfo(name = "jaket") val jaket: Int,
    @ColumnInfo(name = "Selimut") val selimut: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(tanggal)
        parcel.writeInt(baju)
        parcel.writeInt(celana)
        parcel.writeInt(jaket)
        parcel.writeInt(selimut)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LaundryNote> {
        override fun createFromParcel(parcel: Parcel): LaundryNote {
            return LaundryNote(parcel)
        }

        override fun newArray(size: Int): Array<LaundryNote?> {
            return arrayOfNulls(size)
        }
    }
}
