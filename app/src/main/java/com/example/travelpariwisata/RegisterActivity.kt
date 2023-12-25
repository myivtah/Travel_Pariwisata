package com.example.travelpariwisata

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var txtName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtUsername: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtRole: EditText
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
        txtRole = findViewById(R.id.txtRole)
        btnSave = findViewById(R.id.buttonRegis)

        btnSave.setOnClickListener {
            val name = txtName.text.toString()
            val email = txtEmail.text.toString()
            val username = txtUsername.text.toString()
            val password = txtPassword.text.toString()
            val role = txtRole.text.toString()

            if (validateInput(name, email, username, password, role)) {
                checkIfUserExists(email, username, object : UserCheckCallback {
                    override fun onUserCheckComplete(emailExists: Boolean, usernameExists: Boolean) {
                        if (!emailExists && !usernameExists) {
                            registerUser(name, email, username, password, role)
                        } else {
                            showToast("Email atau Username sudah terdaftar")
                        }
                    }
                })
            }
        }
    }

    private fun checkIfUserExists(email: String, username: String, callback: UserCheckCallback) {
        val emailQuery = database.orderByChild("email").equalTo(email)
        val usernameQuery = database.orderByChild("userId").equalTo(username)

        emailQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val emailExists = dataSnapshot.exists()
                usernameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val usernameExists = dataSnapshot.exists()
                        callback.onUserCheckComplete(emailExists, usernameExists)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun registerUser(name: String, email: String, username: String, password: String, role: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val userId = username

                    val userObject = HashMap<String, Any>()
                    userObject["userId"] = userId
                    userObject["name"] = name
                    userObject["email"] = email
                    userObject["role"] = role

                    database.child(userId).setValue(userObject)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                showToast("Registrasi berhasil")
                                navigateToLoginActivity()
                            } else {
                                showToast("Registrasi Gagal, Periksa kembali data anda!")
                            }
                        }
                } else {
                    showToast("Registrasi Gagal, periksa kembali data anda!")
                }
            }
    }

    interface UserCheckCallback {
        fun onUserCheckComplete(emailExists: Boolean, usernameExists: Boolean)
    }

    private fun validateInput(name: String, email: String, username: String, password: String, role: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            showToast("Semua kolom harus diisi")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Format email tidak valid")
            return false
        }

        if (password.length < 6) {
            showToast("Panjang kata sandi harus minimal 6 karakter")
            return false
        }
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

