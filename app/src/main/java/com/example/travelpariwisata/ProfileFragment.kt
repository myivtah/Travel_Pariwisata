package com.example.travelpariwisata

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.ActionMenuView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private lateinit var textProfileName: TextView
    private lateinit var textProfileEmail: TextView
    private lateinit var imageProfilePicture: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        textProfileName = view.findViewById(R.id.textProfileName)
        textProfileEmail = view.findViewById(R.id.textProfileEmail)
        imageProfilePicture = view.findViewById(R.id.imageProfilePicture)

        // Ambil email pengguna yang saat ini masuk
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        // Pastikan email tidak null sebelum mengambil data dari database
        userEmail?.let { email ->
            // Ambil data pengguna dari Firebase Realtime Database berdasarkan email
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")

            // Tambahkan query untuk mencari UID berdasarkan email
            databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Pastikan ada hasil dari query
                        if (snapshot.exists()) {
                            // Ambil UID pengguna (ambil UID pertama jika ada beberapa hasil)
                            val userId = snapshot.children.firstOrNull()?.key

                            // Pastikan UID tidak null sebelum mengambil data dari database
                            userId?.let { uid ->
                                // Ambil data pengguna dari Firebase Realtime Database berdasarkan UID
                                val userReference =
                                    FirebaseDatabase.getInstance().getReference("users").child(uid)

                                // Tambahkan listener untuk mengambil data pengguna
                                userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(userSnapshot: DataSnapshot) {
                                        // Pastikan ada hasil dari query
                                        if (userSnapshot.exists()) {
                                            // Ambil data nama dan email pengguna
                                            val userName =
                                                userSnapshot.child("name").getValue(String::class.java)
                                            val userEmail =
                                                userSnapshot.child("email").getValue(String::class.java)
                                            val userProfilePicturePath =
                                                userSnapshot.child("profilePicturePath")
                                                    .getValue(String::class.java)

                                            // Tampilkan nama dan email
                                            textProfileName.text = userName.orEmpty()
                                            textProfileEmail.text = userEmail.orEmpty()

                                            // Ambil dan tampilkan gambar dari Firebase Storage
                                            userProfilePicturePath?.let {
                                                loadProfilePicture(it)
                                            } ?: run {
                                                // Jika path gambar kosong, set gambar default di sini
                                                // Misalnya, menggunakan gambar default lokal:
                                                setDefaultProfilePicture()
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                })
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }

        // Atur listener untuk action logout
        val logoutView = view.findViewById<View>(R.id.actionLogout)

// Tambahkan OnClickListener pada View tersebut
        logoutView.setOnClickListener {
            // Tampilkan dialog konfirmasi logout
            showLogoutConfirmationDialog()
        }

        return view
    }

    private fun loadProfilePicture(profilePicturePath: String) {
        // Ambil referensi ke Firebase Storage
        val storageReference = FirebaseStorage.getInstance().getReference(profilePicturePath)

        // Ambil gambar dari Firebase Storage dan tampilkan dengan Picasso
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(imageProfilePicture)
        }.addOnFailureListener {
            // Handle kegagalan pengambilan gambar
        }
    }

    private fun setDefaultProfilePicture() {
        // Gunakan gambar default dari folder drawable
        imageProfilePicture.setImageResource(R.drawable.account)
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout Confirmation")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("No") { _, _ ->
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
        navigateToWelcomeActivity()

        // Logout dari Firebase (jika masih digunakan)
        FirebaseAuth.getInstance().signOut()
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(requireActivity(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // Tutup ProfileActivity agar tidak bisa kembali ke halaman ini
    }
}
