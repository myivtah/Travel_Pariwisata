package com.example.travelpariwisata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val profileFragment = ProfileFragment()
        val homeFragment = HomeFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, homeFragment)
            commit()
        }

        val btnHome = findViewById<ImageButton>(R.id.imageButtonHome)
        val btnProfile = findViewById<ImageButton>(R.id.imageButtonProfile)

        btnHome.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, homeFragment)
                commit()
            }
        }

        btnProfile.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, profileFragment)
                commit()
            }
        }
    }
}

