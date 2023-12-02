package com.example.travelpariwisata

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ActionMenuView
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private lateinit var activeButton: ImageButton
    private lateinit var imageHome: ImageButton
    private lateinit var imageStatus: ImageButton
    private lateinit var imageTrip: ImageButton
    lateinit var imageProfile: ImageButton
    private lateinit var actionHome: ActionMenuView
    private lateinit var actionProfile: ActionMenuView
    private lateinit var actionStatus: ActionMenuView
    private lateinit var actionTrip: ActionMenuView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusFragment = StatusFragment()
        val tripFragment = TripFragment()
        val profileFragment = ProfileFragment()
        val homeFragment = HomeFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, homeFragment)
            commit()
        }

        imageHome = findViewById(R.id.imageButtonHome)
        imageProfile = findViewById(R.id.imageButtonProfile)
        imageStatus = findViewById(R.id.imageButtonStatus)
        imageTrip = findViewById(R.id.imageButtonTrip)
        actionHome = findViewById(R.id.actionMenuHome)
        actionProfile = findViewById(R.id.actionMenuProfile)
        actionStatus = findViewById(R.id.actionMenuStatus)
        actionTrip = findViewById(R.id.actionMenuTrip)

        activeButton = imageHome
        applyActiveButtonStyle()

        actionHome.setOnClickListener {
            switchFragment(homeFragment, imageHome)
        }

        actionProfile.setOnClickListener {
            switchFragment(profileFragment, imageProfile)
        }

        actionStatus.setOnClickListener {
            switchFragment(statusFragment, imageStatus)
        }

        actionTrip.setOnClickListener {
            switchFragment(tripFragment, imageTrip)
        }
    }

    fun switchFragment(fragment: Fragment, button: ImageButton) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
            clearActiveButtonStyle()
            activeButton = button
            applyActiveButtonStyle()
        }
    }

    private fun applyActiveButtonStyle() {
        activeButton.setColorFilter(ContextCompat.getColor(this, R.color.red))
    }


    private fun clearActiveButtonStyle() {
        activeButton.clearColorFilter()
    }
}
