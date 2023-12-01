package com.example.travelpariwisata

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var auth: FirebaseAuth = FirebaseAuth.getInstance()

        var txtEmail: EditText = findViewById(R.id.txtEmail)
        var txtPassword: EditText = findViewById(R.id.txtPassword)
        var btnLogin: Button = findViewById(R.id.buttonLogin)
        var txtRegis: TextView = findViewById(R.id.textButtonReg)

        txtRegis.setOnClickListener {
            val intentReg  = Intent(this, RegisterActivity::class.java)
            startActivity(intentReg)
        }

        btnLogin.setOnClickListener {
            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Harap isi semua kolom")
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            showToast("Login berhasil")
                            navigateToMainActivity()
                        } else {
                            showToast("Login Gagal. ${task.exception?.message}")
                        }
                    }
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
