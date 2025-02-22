package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dots: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for existing auth token before setting content view
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        // If auth token exists, redirect to MainActivity
        if (authToken != null) {
            val intent = Intent(this@OnBoardingActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Close OnBoardingActivity so user can't go back to it
            return  // Exit onCreate to prevent setting up onboarding UI
        }

        setContentView(R.layout.activity_onboarding01)

        // Initialize ViewPager2 and dots
        viewPager = findViewById(R.id.viewPager)
        dots = listOf(
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3)
        )

        // Set up images for onboarding
        val images = mutableListOf(
            R.drawable.onboarding_img1,
            R.drawable.onboarding_img2,
            R.drawable.onboarding_img3
        )

        // Set up adapter for ViewPager2
        val adapter = SliderAdapter(images)
        viewPager.adapter = adapter

        // Handle page change events
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
        })

        // Set up "Create Account" button
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            val intent = Intent(this@OnBoardingActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        // Set up "Already Have Account" button
        val alreadyHaveAccountText = findViewById<Button>(R.id.alreadyHaveAccountText)
        alreadyHaveAccountText.setOnClickListener {
            val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // Update dot indicators based on the selected page
    private fun updateDots(position: Int) {
        dots.forEachIndexed { index, view ->
            view.setBackgroundColor(
                resources.getColor(
                    if (index == position) android.R.color.black else android.R.color.darker_gray,
                    theme
                )
            )
        }
    }
}
