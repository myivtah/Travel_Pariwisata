package com.example.travelpariwisata

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin: Button = findViewById(R.id.buttonLogin)
        val txtUsername: EditText = findViewById(R.id.txtUsername)
        val txtPassword: EditText = findViewById(R.id.txtPassword)
        val logObj = Login()

        btnLogin.setOnClickListener {
            logObj.username = txtUsername.text.toString()
            logObj.password = txtPassword.text.toString()

            if (logObj.username == "Miftah" && logObj.password == "1") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Login gagal, cek username dan password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}