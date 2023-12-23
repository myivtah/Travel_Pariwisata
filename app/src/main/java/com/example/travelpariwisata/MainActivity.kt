package com.example.travelpariwisata

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private val homeFragment = HomeFragment()
    private val statusFragment = TicketFragment()
    private val tripFragment = TripFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragmentHome -> {
                    switchFragment(homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.fragmentStatus -> {
                    switchFragment(statusFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.fragmentTrip -> {
                    switchFragment(tripFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.fragmentProfile -> {
                    switchFragment(profileFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        switchFragment(homeFragment)
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}
