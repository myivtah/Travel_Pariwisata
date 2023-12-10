package com.example.travelpariwisata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PaketAdapter(private val paketList: MutableList<Map<String, String>>) : RecyclerView.Adapter<PaketAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_paket, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paket = paketList[position]
        holder.bind(paket)
    }

    override fun getItemCount(): Int {
        return paketList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageWisata: ImageView = itemView.findViewById(R.id.imageWisata)
        private val textJudul: TextView = itemView.findViewById(R.id.TextJudul)
        private val textViewHarga: TextView = itemView.findViewById(R.id.textViewHarga)

        fun bind(paket: Map<String, String>) {
            textJudul.text = paket["judul"]
            textViewHarga.text = paket["harga"]

            // Load gambar menggunakan Picasso atau cara lainnya
            val imageUrl = paket["gambarUrl"]
            if (!imageUrl.isNullOrBlank()) {
                Picasso.get().load(imageUrl).into(imageWisata)
            }
        }
    }

    // Metode untuk memperbarui data pada adapter
    fun updateData(newData: List<Map<String, String>>) {
        paketList.clear()
        paketList.addAll(newData)
        notifyDataSetChanged()
    }
}
