package com.example.travelpariwisata.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpariwisata.R

class TransaksiAdapter(private val transaksiList: List<HashMap<String, Any>>, private val listener: TransaksiAdapterListener) :
    RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder>() {

    interface TransaksiAdapterListener {
        fun onBatalButtonClicked(position: Int)
        fun onBayarButtonClicked(position: Int)
    }

    class TransaksiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTrans: TextView = itemView.findViewById(R.id.textViewIdTrans)
        val namaPemesan: TextView = itemView.findViewById(R.id.textViewNamaPemesan)
        val tanggalTransaksi: TextView = itemView.findViewById(R.id.textViewTanggalTransaksi)
        val paket: TextView = itemView.findViewById(R.id.textViewPaket)
        val harga: TextView = itemView.findViewById(R.id.textViewHarga)
        val tax: TextView = itemView.findViewById(R.id.textViewTax)
        val noIdPemesan: TextView = itemView.findViewById(R.id.textViewNoIdPemesan)
        val noTelpPemesan: TextView = itemView.findViewById(R.id.textViewNoTelpPemesan)
        val alamatPemesan: TextView = itemView.findViewById(R.id.textViewAlamatPemesan)
        val totalHarga: TextView = itemView.findViewById(R.id.textViewTotalHarga)
        val jumlahPeserta: TextView = itemView.findViewById(R.id.textViewJumlahPeserta)
        val totalBayar: TextView = itemView.findViewById(R.id.textViewTotalBayar)
        val buttonBatal: Button = itemView.findViewById(R.id.buttonBatal)
        val buttonBayar: Button = itemView.findViewById(R.id.buttonBayar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item_transaksi, parent, false)
        return TransaksiViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        val transaksiData = transaksiList[position]

        holder.idTrans.text = transaksiData["id_trans"].toString()
        holder.namaPemesan.text = transaksiData["NamaPemesan"].toString()
        holder.tanggalTransaksi.text = "${transaksiData["TanggalTransaksi"]}"
        holder.paket.text = transaksiData["Paket"].toString()
        holder.harga.text = "Rp. ${transaksiData["Harga"]}"
        holder.jumlahPeserta.text = "${transaksiData["JumlahPeserta"]}"
        val peserta: Int = transaksiData["JumlahPeserta"]?.toString()?.toIntOrNull() ?: 0
        val harga: Double = when (val hargaRaw = transaksiData["Harga"]) {
            is Long -> hargaRaw.toDouble()
            is Double -> hargaRaw
            else -> 0.0
        }
        val totalHarga = (peserta * harga).toInt()
        holder.totalHarga.text = totalHarga.toString()
        val tax = (totalHarga * 0.11).toInt()
        holder.tax.text = "Rp. ${tax}"
        holder.noIdPemesan.text = transaksiData["NoIdPemesan"].toString()
        holder.noTelpPemesan.text = transaksiData["NoTelpPemesan"].toString()
        holder.alamatPemesan.text = transaksiData["AlamatPemesan"].toString()
        val totalBayar = (totalHarga + tax).toInt()
        holder.totalBayar.text = "Rp. ${totalBayar}"

        holder.buttonBatal.setOnClickListener {
            listener.onBatalButtonClicked(position)
        }

        holder.buttonBayar.setOnClickListener {
            listener.onBayarButtonClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return transaksiList.size
    }
}
