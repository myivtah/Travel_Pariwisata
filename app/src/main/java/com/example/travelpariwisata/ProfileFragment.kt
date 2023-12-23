package com.example.travelpariwisata

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileFragment : Fragment() {
    private lateinit var textProfileName: TextView
    private lateinit var textProfileEmail: TextView
    private lateinit var imageProfilePicture: ImageView
    private lateinit var adminMenu: View
    private lateinit var layoutAdmin: ConstraintLayout
    private lateinit var storageReference: StorageReference

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        textProfileName = view.findViewById(R.id.textProfileName)
        textProfileEmail = view.findViewById(R.id.textProfileEmail)
        imageProfilePicture = view.findViewById(R.id.imageProfilePicture)
        adminMenu = view.findViewById(R.id.actionAdmin)
        layoutAdmin = view.findViewById(R.id.constraintLayoutAdmin)

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

        storageReference = FirebaseStorage.getInstance().getReference("profile_images")

        val logoutView = view.findViewById<View>(R.id.actionLogout)

        logoutView.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        adminMenu.setOnClickListener {
            navigateToAdminFragment()
        }

        imageProfilePicture.setOnClickListener {
            openGallery()
        }

        return view
    }

    private fun loadDataFromFirebase(
        email: String,
        sharedPreferences: SharedPreferences
    ) {
        val userReference = FirebaseDatabase.getInstance().getReference("users")

        userReference.orderByChild("email").equalTo(email)
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
                                        val userRole =
                                            userSnapshot.child("role").getValue(String::class.java)
                                                .toString()
                                        val userProfilePicturePath =
                                            userSnapshot.child("profileUrl")
                                                .getValue(String::class.java)

                                        saveDataToSharedPreferences(
                                            userName,
                                            userEmail,
                                            userProfilePicturePath,
                                            userRole,
                                            sharedPreferences
                                        )

                                        setAdminVisibility(userRole)
                                        loadProfilePicture(userProfilePicturePath)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun setAdminVisibility(userRole: String?) {
        if (userRole == "Admin") {
            // User is an admin
            layoutAdmin.visibility = View.VISIBLE
        } else {
            // User is not an admin
            layoutAdmin.visibility = View.GONE
        }
    }

    private fun readDataFromSharedPreferences(sharedPreferences: SharedPreferences) {
        val userName = sharedPreferences.getString("userName", "").orEmpty()
        val userEmail = sharedPreferences.getString("userEmail", "").orEmpty()
        val userProfilePicturePath = sharedPreferences.getString("userProfilePicture", "").orEmpty()
        val userRole = sharedPreferences.getString("userRole", "").orEmpty()

        setUserData(userName, userEmail)
        setAdminVisibility(userRole)
        loadProfilePicture(userProfilePicturePath)
    }

    private fun saveDataToSharedPreferences(
        userName: String?,
        userEmail: String?,
        userProfilePicturePath: String?,
        userRole: String?,
        sharedPreferences: SharedPreferences
    ) {
        val editor = sharedPreferences.edit()
        editor.putString("userName", userName.orEmpty())
        editor.putString("userEmail", userEmail.orEmpty())
        editor.putString("userProfilePicture", userProfilePicturePath.orEmpty())
        editor.putString("userRole", userRole.orEmpty())
        editor.putBoolean("isUserDataLoaded", true)
        editor.apply()

        setUserData(userName.orEmpty(), userEmail.orEmpty())
        setAdminVisibility(userRole)
        loadProfilePicture(userProfilePicturePath)
    }

    private fun setUserData(userName: String, userEmail: String) {
        textProfileName.text = userName
        textProfileEmail.text = userEmail
    }

    private fun loadProfilePicture(profilePicturePath: String?) {
        if (profilePicturePath != null && profilePicturePath.isNotEmpty()) {
            Glide.with(this)
                .load(profilePicturePath)
                .skipMemoryCache(true) // Skip memory cache to force reload
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageProfilePicture)
        } else {
            setDefaultProfilePicture()
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!

            // Upload gambar ke Firebase Storage
            uploadImageToFirebaseStorage(imageUri)
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val imageName = "profile_image.jpg" // Nama gambar di Firebase Storage
        val imageReference = storageReference.child(imageName)

        imageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Gambar berhasil diupload
                imageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Simpan URL gambar di Firebase Realtime Database
                    saveImageUrlToDatabase(imageUrl)
                }
            }
            .addOnFailureListener { e ->
                // Gagal mengupload gambar
                Toast.makeText(requireContext(), "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        userEmail?.let { email ->
            val userReference = FirebaseDatabase.getInstance().getReference("users")

            userReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val userId = snapshot.children.firstOrNull()?.key

                            userId?.let { uid ->
                                val userReference =
                                    FirebaseDatabase.getInstance().getReference("users").child(uid)

                                // Sisipkan URL gambar ke kolom baru "profileUrl"
                                userReference.child("profileUrl").setValue(imageUrl)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Simpan path gambar ke SharedPreferences
                                            saveImagePathToLocal(imageUrl)

                                            // Tampilkan gambar yang sudah diupload
                                            loadProfilePicture(imageUrl)
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Failed to save image URL to database",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }

    private fun saveImagePathToLocal(imageUrl: String) {
        // Simpan path gambar ke SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userProfilePicture", imageUrl)
        editor.apply()
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

    private fun setDefaultProfilePicture() {
        imageProfilePicture.setImageResource(R.drawable.account)
    }
}
