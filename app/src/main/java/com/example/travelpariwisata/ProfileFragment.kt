package com.example.travelpariwisata

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
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

        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        userEmail?.let { email ->
            val sharedPreferences =
                requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val isUserDataLoaded = sharedPreferences.getBoolean("isUserDataLoaded", false)

            if (!isUserDataLoaded) {
                loadDataFromFirebase(email, sharedPreferences)
            } else {
                readDataFromSharedPreferences(sharedPreferences)
            }
        }

        val logoutView = view.findViewById<View>(R.id.actionLogout)

        logoutView.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        val adminMenu = view.findViewById<View>(R.id.actionAdmin)

        adminMenu.setOnClickListener{
            navigateToAdminFragment()
        }
        return view
    }

    private fun loadDataFromFirebase(
        email: String,
        sharedPreferences: SharedPreferences
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userId = snapshot.children.firstOrNull()?.key

                        userId?.let { uid ->
                            val userReference =
                                FirebaseDatabase.getInstance().getReference("users").child(uid)

                            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    if (userSnapshot.exists()) {
                                        val userName =
                                            userSnapshot.child("name").getValue(String::class.java)
                                        val userEmail =
                                            userSnapshot.child("email").getValue(String::class.java)
                                        val userProfilePicturePath =
                                            userSnapshot.child("profilePicturePath")
                                                .getValue(String::class.java)

                                        // Simpan data pengguna ke SharedPreferences
                                        saveDataToSharedPreferences(
                                            userName,
                                            userEmail,
                                            userProfilePicturePath,
                                            sharedPreferences
                                        )
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

    private fun readDataFromSharedPreferences(sharedPreferences: SharedPreferences) {
        val userName = sharedPreferences.getString("userName", "").orEmpty()
        val userEmail = sharedPreferences.getString("userEmail", "").orEmpty()
        val userProfilePicturePath = sharedPreferences.getString("userProfilePicture", "").orEmpty()

        // Tampilkan data pengguna
        setUserData(userName, userEmail, userProfilePicturePath)
    }

    private fun saveDataToSharedPreferences(
        userName: String?,
        userEmail: String?,
        userProfilePicturePath: String?,
        sharedPreferences: SharedPreferences
    ) {
        val editor = sharedPreferences.edit()
        editor.putString("userName", userName.orEmpty())
        editor.putString("userEmail", userEmail.orEmpty())
        editor.putString("userProfilePicture", userProfilePicturePath.orEmpty())
        editor.putBoolean("isUserDataLoaded", true) // Tandai bahwa data sudah dimuat
        editor.apply()

        // Tampilkan data pengguna
        setUserData(userName.orEmpty(), userEmail.orEmpty(), userProfilePicturePath.orEmpty())
    }

    private fun setUserData(userName: String, userEmail: String, userProfilePicturePath: String) {
        textProfileName.text = userName
        textProfileEmail.text = userEmail

        userProfilePicturePath.let {
            loadProfilePicture(it)
        } ?: run {
            setDefaultProfilePicture()
        }
    }

    private fun loadProfilePicture(profilePicturePath: String) {
        if (profilePicturePath.isNotEmpty()) {
            val storageReference = FirebaseStorage.getInstance().getReference(profilePicturePath)

            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(imageProfilePicture)
            }.addOnFailureListener {
                setDefaultProfilePicture()
            }
        } else {
            setDefaultProfilePicture()
        }
    }

    private fun setDefaultProfilePicture() {
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
        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        FirebaseAuth.getInstance().signOut()

        requireActivity().runOnUiThread {
            navigateToWelcomeActivity()
        }
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(requireActivity(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToAdminFragment() {
        val adminFragment = AdminFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, adminFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
