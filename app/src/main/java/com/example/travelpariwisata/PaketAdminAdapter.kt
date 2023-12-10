package com.example.travelpariwisata

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpariwisata.menu.PaketModel
import com.squareup.picasso.Picasso

class PaketAdminAdapter(private val context: Context, private val paketList: List<PaketModel>) :
    RecyclerView.Adapter<PaketAdminAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageWisataAdmin)
        val judulTextView: TextView = view.findViewById(R.id.textJudulAdmin)
        val hargaTextView: TextView = view.findViewById(R.id.textViewHargaAdmin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_paket_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paket = paketList[position]

        // Set data to views
        Picasso.get().load(paket.imageUrl).into(holder.imageView)
        holder.judulTextView.text = paket.name
        holder.hargaTextView.text = paket.harga.toString()
    }

    override fun getItemCount(): Int {
        return paketList.size
    }
}
