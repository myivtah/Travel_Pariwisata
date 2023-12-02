package com.example.travelpariwisata

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ActionMenuView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.example.travelpariwisata.LoginActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var actionMenuView: ActionMenuView

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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        actionMenuView = view.findViewById(R.id.actionLogout)

        actionMenuView.setOnClickListener {
            showLogoutConfirmationDialog()
        }
        return view
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout Confirmation")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, which ->
                performLogout()
            }
            .setNegativeButton("No") { dialog, which ->
            }
            .show()
    }

    private fun performLogout() {
        // Menghapus status login dari penyimpanan lokal
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // Kembali ke WelcomeActivity setelah logout
        val intent = Intent(requireActivity(), WelcomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Tutup MainActivity agar tidak bisa kembali ke halaman ini

        // Logout dari Firebase (jika masih digunakan)
        FirebaseAuth.getInstance().signOut()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
