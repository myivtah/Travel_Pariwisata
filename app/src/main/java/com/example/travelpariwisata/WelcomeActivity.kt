package com.example.travelpariwisata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnNext : Button = findViewById(R.id.buttonNext)

        btnNext.setOnClickListener{
            val intentNext = Intent(this, LoginActivity::class.java)
            startActivity(intentNext)
        }
    }
}