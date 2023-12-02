package com.example.travelpariwisata

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnNext: Button = findViewById(R.id.buttonNext)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Pengguna sudah login, buka MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Tutup WelcomeActivity agar tidak bisa kembali ke halaman ini
        } else {
            // Pengguna belum login, tampilkan halaman welcome
            btnNext.setOnClickListener {
                val intentNext = Intent(this, LoginActivity::class.java)
                startActivity(intentNext)
            }
        }
    }
}
