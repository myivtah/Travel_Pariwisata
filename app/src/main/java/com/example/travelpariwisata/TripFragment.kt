package com.example.travelpariwisata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpariwisata.menu.PesananModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TripFragment : Fragment() {

    private lateinit var pesananAdapter: PesananAdapter
    private lateinit var pesananList: MutableList<PesananModel>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip, container, false)

        pesananList = mutableListOf()
        pesananAdapter = PesananAdapter(requireContext(), pesananList)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerTrip)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pesananAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("pesanan")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pesananList.clear()
                for (pesananSnapshot in snapshot.children) {
                    val pesananValue = pesananSnapshot.getValue()
                    if (pesananValue is Long) {
                        val pesananModel = PesananModel(harga = pesananValue.toInt())
                        pesananList.add(pesananModel)
                    } else {
                        val pesanan = pesananSnapshot.getValue(PesananModel::class.java)
                        pesanan?.let {
                            pesananList.add(it)
                        }
                    }
                }
                pesananAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        databaseReference.addValueEventListener(valueEventListener)
        return view
    }
}

