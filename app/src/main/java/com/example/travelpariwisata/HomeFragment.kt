package com.example.travelpariwisata

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val rvHome: RecyclerView = view.findViewById(R.id.recyclerPaket)
        rvHome.layoutManager = GridLayoutManager(activity, 2)

        val btnUser = view.findViewById<ImageView>(R.id.imageUser)
        val btnNotification = view.findViewById<ImageView>(R.id.imageNotification)

        btnUser.setOnClickListener {
            val profileFragment = ProfileFragment()
            val mainActivity = activity as? MainActivity
            mainActivity?.switchFragment(profileFragment, mainActivity.imageProfile)
        }
        btnNotification.setOnClickListener {
            val notificationFragment = NotificationFragment()
            val mainActivity = activity as? MainActivity
            mainActivity?.switchFragment(notificationFragment, mainActivity.imageProfile)
        }

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
