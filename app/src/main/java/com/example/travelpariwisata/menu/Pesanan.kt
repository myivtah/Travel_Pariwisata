package com.example.travelpariwisata.menu

import android.os.Parcel
import android.os.Parcelable

data class Pesanan(
    val idPesanan: String,
    val IdTransaksi: String,
    val namaPemesan: String,
    val noIdPemesan: String,
    val noTelpPemesan: String,
    val alamatPemesan: String,
    val paket: String,
    val harga: Int,
    val deskripsi: String,
    val emailPemesan: String,
    val userIdPemesan: String,
    val tanggalPesanan: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readInt() ?: 0,
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idPesanan)
        parcel.writeString(IdTransaksi)
        parcel.writeString(namaPemesan)
        parcel.writeString(noIdPemesan)
        parcel.writeString(noTelpPemesan)
        parcel.writeString(alamatPemesan)
        parcel.writeString(paket)
        parcel.writeInt(harga)
        parcel.writeString(deskripsi)
        parcel.writeString(emailPemesan)
        parcel.writeString(userIdPemesan)
        parcel.writeString(tanggalPesanan)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pesanan> {
        override fun createFromParcel(parcel: Parcel): Pesanan {
            return Pesanan(parcel)
        }

        override fun newArray(size: Int): Array<Pesanan?> {
            return arrayOfNulls(size)
        }
    }
}
