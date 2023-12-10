// AdminFragment.kt

package com.example.travelpariwisata

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpariwisata.menu.PaketModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminFragment : Fragment() {

    private lateinit var paketAdminAdapter: PaketAdminAdapter
    private lateinit var paketList: MutableList<PaketModel>
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Paket")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        val rvHome: RecyclerView = view.findViewById(R.id.recyclerPaketAdmin)
        rvHome.layoutManager = GridLayoutManager(requireActivity(), 1)

        paketList = mutableListOf()
        paketAdminAdapter = PaketAdminAdapter(requireContext(), paketList)
        rvHome.adapter = paketAdminAdapter

        val btnTambah: Button = view.findViewById(R.id.buttonTambah)

        btnTambah.setOnClickListener {
            val intent = Intent(requireContext(), TambahPaketActivity::class.java)
            startActivity(intent)
        }

        // Menanggapi aksi penghapusan
        paketAdminAdapter.setOnItemClickListener { paket ->
            // Ambil URL gambar dari Firebase Realtime Database
            val imageUrl = paket.imageUrl

            // Hapus data dari Firebase Realtime Database
            databaseReference.child(paket.id).removeValue()

            // Hapus gambar dari Firebase Storage
            deleteImageFromStorage(imageUrl, paket.id)
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                paketList.clear()
                for (dataSnapshot in snapshot.children) {
                    val paket = dataSnapshot.getValue(PaketModel::class.java)
                    paket?.let { paketList.add(it) }
                }
                paketAdminAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return view
    }

    private fun deleteImageFromStorage(imageUrl: String, paketId: String) {
        // Hapus gambar dari Firebase Storage (contoh)
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete().addOnSuccessListener {
            Toast.makeText(
                requireContext(),
                "Data dihapus",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Gagal menghapus gambar: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
