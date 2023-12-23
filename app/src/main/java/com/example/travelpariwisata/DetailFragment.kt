package com.example.travelpariwisata

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.travelpariwisata.menu.PaketModel
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "param1"

class DetailFragment : Fragment() {

    private var paketModel: PaketModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paketModel = it.getSerializable(ARG_PARAM1) as PaketModel?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        paketModel?.let {
            // Tampilkan gambar menggunakan Picasso
            Picasso.get().load(it.imageUrl).into(view.findViewById<ImageView>(R.id.imageDetail))

            // Set data ke TextViews
            view.findViewById<TextView>(R.id.textViewPaketDetail).text = it.name
            view.findViewById<TextView>(R.id.textViewHargaDetail).text = it.harga.toString()
            view.findViewById<TextView>(R.id.textViewDeskripsiDetail).text = it.deskripsi
        }

        // Mendapatkan referensi ke buttonPesan
        val buttonPesan = view.findViewById<Button>(R.id.buttonPesan)

        // Menambahkan OnClickListener ke buttonPesan
        buttonPesan.setOnClickListener {
            // Membuat intent untuk pindah ke Activity Transaksi
            val intent = Intent(activity, TransaksiActivity::class.java)

            // Menambahkan data yang diperlukan ke intent (contoh: paketModel)
            intent.putExtra("paketModel", paketModel)

            // Memulai Activity Transaksi
            startActivity(intent)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(paketModel: PaketModel) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, paketModel)
                }
            }
    }
}
