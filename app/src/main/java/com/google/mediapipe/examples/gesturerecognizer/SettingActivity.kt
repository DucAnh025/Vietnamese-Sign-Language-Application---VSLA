package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityWithNavBinding

class SettingActivity : FragmentActivity() {

    private lateinit var binding: ActivityWithNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set content for the HomeActivity
        layoutInflater.inflate(R.layout.activity_setting, binding.container, true)

        // Setup bottom navigation
        binding.bottomNavigationView.selectedItemId = R.id.practice
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.planet -> {
                    startActivity(Intent(this, TaiLieuActivity::class.java))
                    true
                }
                R.id.practice -> {
                    startActivity(Intent(this, PracticeActivity::class.java))
                    true
                }
                R.id.setting -> true
                else -> false
            }
        }

        // Button to edit profile
        findViewById<Button>(R.id.btn_edit_profile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        findViewById<Button>(R.id.btn_progress).setOnClickListener {
            startActivity(Intent(this, ProgressActivity::class.java))
        }
        // Button to edit profile
        findViewById<Button>(R.id.btn_watchedvideos).setOnClickListener {
            startActivity(Intent(this, WatchedVideosActivity::class.java))
        }

        // Button to change password
        findViewById<Button>(R.id.btn_change_password).setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        // Button to notification
        findViewById<Button>(R.id.btn_notification).setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        // Button to notification setting
        findViewById<Button>(R.id.btn_notification_setting).setOnClickListener {
            startActivity(Intent(this, NotificationSettingActivity::class.java))
        }

        // Button to security
        findViewById<Button>(R.id.btn_security).setOnClickListener {
            startActivity(Intent(this, SecurityActivity::class.java))
        }

        // Button to language
        findViewById<Button>(R.id.btn_language).setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        // Button to legal and policies
        findViewById<Button>(R.id.btn_legal_policies).setOnClickListener {
            startActivity(Intent(this, LegalAndPoliciesActivity::class.java))
        }

        // Button to help and support
        findViewById<Button>(R.id.btn_help_support).setOnClickListener {
            startActivity(Intent(this, HelpAndSupportActivity::class.java))
        }

        // Button to logout
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            // Remove auth token from SharedPreferences
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("auth_token")
            editor.apply()

            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish() // Close the SettingActivity
        }
    }
}
