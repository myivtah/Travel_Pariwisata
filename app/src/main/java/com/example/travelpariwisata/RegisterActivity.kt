package com.example.travelpariwisata

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var txtName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtUsername: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        txtName = findViewById(R.id.txtName)
        txtEmail = findViewById(R.id.txtEmail)
        txtUsername = findViewById(R.id.txtUsername)
        txtPassword = findViewById(R.id.txtPassword)
        btnSave = findViewById(R.id.buttonRegis)

        btnSave.setOnClickListener {
            val name = txtName.text.toString()
            val email = txtEmail.text.toString()
            val username = txtUsername.text.toString()
            val password = txtPassword.text.toString()

            if (validateInput(name, email, username, password)) {
                // Menambahkan pengguna ke Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val userId = username

                            val userObject = HashMap<String, Any>()
                            userObject["userId"] = userId
                            userObject["name"] = name
                            userObject["email"] = email

                            // Menambahkan informasi pengguna ke Firebase Realtime Database
                            database.child(userId).setValue(userObject)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        showToast("Registrasi berhasil")
                                        navigateToLoginActivity()
                                    } else {
                                        showToast("Registrasi Gagal di Database. ${dbTask.exception?.message}")
                                    }
                                }
                        } else {
                            showToast("Registrasi Gagal di Authentication. ${authTask.exception?.message}")
                        }
                    }
            }
        }
    }

    private fun validateInput(name: String, email: String, username: String, password: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showToast("Semua kolom harus diisi")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Format email tidak valid")
            return false
        }

        // Tambahkan validasi lain sesuai kebutuhan Anda

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
