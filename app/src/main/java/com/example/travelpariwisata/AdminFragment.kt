package com.example.travelpariwisata

import android.annotation.SuppressLint
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminFragment : Fragment() {

    private lateinit var paketAdminAdapter: PaketAdminAdapter
    private lateinit var paketList: MutableList<PaketModel>
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Paket")

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        val rvHome: RecyclerView = view.findViewById(R.id.recyclerPaketAdmin)
        rvHome.layoutManager = GridLayoutManager(requireActivity(), 1)

        // Buat instance dari adapter dan hubungkan dengan RecyclerView
        paketList = mutableListOf()
        paketAdminAdapter = PaketAdminAdapter(requireContext(), paketList)
        rvHome.adapter = paketAdminAdapter

        val btnTambah: Button = view.findViewById(R.id.buttonTambah) // Ganti dengan ID yang sesuai

        btnTambah.setOnClickListener {
            // Intent untuk berpindah ke activity tambah paket
            val intent = Intent(requireContext(), TambahPaketActivity::class.java)
            startActivity(intent)
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                paketList.clear() // Bersihkan list sebelum menambahkan data baru
                for (dataSnapshot in snapshot.children) {
                    val paket = dataSnapshot.getValue(PaketModel::class.java)
                    paket?.let { paketList.add(it) }
                }
                paketAdminAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(
                    requireContext(),
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminFragment.
         */
        // TODO: Rename and change types and number of parameters
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