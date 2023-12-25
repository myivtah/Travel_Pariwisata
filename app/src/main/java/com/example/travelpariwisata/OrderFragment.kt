package com.example.travelpariwisata

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpariwisata.adapter.TransaksiAdapter
import com.example.travelpariwisata.menu.Pesanan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class OrderFragment : Fragment(), TransaksiAdapter.TransaksiAdapterListener {

    private lateinit var transaksiAdapter: TransaksiAdapter
    private lateinit var transaksiList: MutableList<HashMap<String, Any>>
    private lateinit var transaksiRef: DatabaseReference
    private lateinit var pesananRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerTransaksi)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        transaksiList = mutableListOf()
        transaksiAdapter = TransaksiAdapter(transaksiList, this)
        recyclerView.adapter = transaksiAdapter

        transaksiRef = FirebaseDatabase.getInstance().getReference("transaksi")
        pesananRef = FirebaseDatabase.getInstance().getReference("pesanan")
        auth = FirebaseAuth.getInstance()

        getDataFromFirebase()

        return view
    }

    private fun getDataFromFirebase() {
        val data = mutableListOf<HashMap<String, Any>>()

        transaksiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                data.clear()

                val currentUserEmail = auth.currentUser?.email

                for (transaksiSnapshot in snapshot.children) {
                    val transaksiData = transaksiSnapshot.value
                    if (transaksiData is HashMap<*, *>) {
                        val transaksiEmail = transaksiData["EmailPemesan"] as? String
                        if (transaksiEmail == currentUserEmail) {
                            data.add(transaksiData as HashMap<String, Any>)
                        }
                    }
                }

                transaksiList.clear()
                transaksiList.addAll(data)
                transaksiAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    override fun onBatalButtonClicked(position: Int) {
        showCancellationDialog(position)
    }

    private fun showCancellationDialog(position: Int) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Konfirmasi Pembatalan")
        builder.setMessage("Apakah Anda yakin ingin membatalkan transaksi ini?")
        builder.setPositiveButton("Ya") { _, _ ->
            onBatalConfirmed(position)
        }
        builder.setNegativeButton("Tidak") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun onBatalConfirmed(position: Int) {
        val transaksiData = transaksiList[position]
        val idTransaksi = transaksiData["id_trans"].toString()

        transaksiRef.child(idTransaksi).removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Transaksi berhasil dibatalkan", Toast.LENGTH_SHORT).show()
                transaksiList.removeAt(position)
                transaksiAdapter.notifyItemRemoved(position)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal membatalkan transaksi", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBayarButtonClicked(position: Int) {
        val transaksiData = transaksiList[position]
        val idTransaksi = transaksiData["id_trans"].toString()
        val harga: Double = when (val hargaRaw = transaksiData["Harga"]) {
            is Long -> hargaRaw.toDouble()
            is Double -> hargaRaw
            else -> 0.0
        }
        val peserta: Int = transaksiData["JumlahPeserta"]?.toString()?.toIntOrNull() ?: 0
        val totalHarga = (harga * peserta).toInt()
        val tax = (totalHarga * 0.11).toInt()
        val totalBayar = totalHarga + tax

        val pesanan = Pesanan(
            idTransaksi,
            transaksiData["NamaPemesan"] as String,
            transaksiData["NoIdPemesan"] as String,
            transaksiData["NoTelpPemesan"] as String,
            transaksiData["AlamatPemesan"] as String,
            transaksiData["Paket"] as String,
            totalBayar.toInt(),
            transaksiData["Deskripsi"] as String,
            transaksiData["EmailPemesan"] as String,
            transaksiData["UserIdPemesan"] as String,
            getCurrentDateTimeInJakarta()
        )

        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra("pesanan", pesanan)
        startActivity(intent)
    }


    private fun getCurrentDateTimeInJakarta(): String {
        val timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = timeZone
        return dateFormat.format(Date())
    }
}
