package com.example.travelpariwisata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpariwisata.menu.PaketModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private lateinit var paketAdapter: PaketAdapter
    private lateinit var paketList: MutableList<PaketModel>
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Paket")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val rvHome: RecyclerView = view.findViewById(R.id.recyclerPaket)
        rvHome.layoutManager = GridLayoutManager(requireActivity(), 1)

        // Buat instance dari adapter dan hubungkan dengan RecyclerView
        paketList = mutableListOf()
        paketAdapter = PaketAdapter(requireContext(), paketList)
        rvHome.adapter = paketAdapter

        val btnUser = view.findViewById<ImageView>(R.id.imageUser)
        val btnNotification = view.findViewById<ImageView>(R.id.imageNotification)

        btnUser.setOnClickListener {
            val profileFragment = ProfileFragment()
            val mainActivity = requireActivity() as? MainActivity
            mainActivity?.switchFragment(profileFragment, mainActivity.imageProfile)
        }
        btnNotification.setOnClickListener {
            val notificationFragment = NotificationFragment()
            val mainActivity = requireActivity() as? MainActivity
            mainActivity?.switchFragment(notificationFragment, mainActivity.imageProfile)
        }

        // Ambil data dari Firebase Realtime Database atau sumber data lainnya
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                paketList.clear() // Bersihkan list sebelum menambahkan data baru
                for (dataSnapshot in snapshot.children) {
                    val paket = dataSnapshot.getValue(PaketModel::class.java)
                    paket?.let { paketList.add(it) }
                }
                paketAdapter.notifyDataSetChanged()
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
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

