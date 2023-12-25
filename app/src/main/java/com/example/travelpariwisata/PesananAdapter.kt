package com.example.travelpariwisata

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpariwisata.menu.PesananModel
import com.google.android.material.card.MaterialCardView

class PesananAdapter(private val context: Context, private val pesananList: List<PesananModel>) :
    RecyclerView.Adapter<PesananAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view.findViewById(R.id.cardviewTrip)
        val namaTextView = view.findViewById<TextView>(R.id.namaPemesan)
        val alamatTextView = view.findViewById<TextView>(R.id.alamatPemesan)
        val emailTextView = view.findViewById<TextView>(R.id.emailPemesan)
        val identitasTextView = view.findViewById<TextView>(R.id.identitasPemesan)
        val hpTextView = view.findViewById<TextView>(R.id.hpPemesan)
        val paketTextView = view.findViewById<TextView>(R.id.paketPemesan)
        val tanggalTextView = view.findViewById<TextView>(R.id.tanggalPemesan)
        val hargaTextView = view.findViewById<TextView>(R.id.hargaPesanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_trip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pesanan = pesananList[position]

        holder.namaTextView.text = pesanan.namaPemesan
        holder.alamatTextView.text = pesanan.alamatPemesan
        holder.emailTextView.text = pesanan.emailPemesan
        holder.identitasTextView.text = pesanan.noIdPemesan
        holder.hpTextView.text = pesanan.noTelpPemesan
        holder.paketTextView.text = pesanan.paket
        holder.tanggalTextView.text = pesanan.tanggalPesanan

        val harga = when (pesanan.harga) {
            is Long -> pesanan.harga.toString()
            is String -> pesanan.harga
            else -> ""
        }
        holder.hargaTextView.text = "Rp.$harga"

        holder.cardView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return pesananList.size
    }
}
