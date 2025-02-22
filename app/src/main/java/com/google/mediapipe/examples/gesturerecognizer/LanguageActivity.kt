package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class LanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        val btnBack: ImageButton = findViewById(R.id.backButton)
        btnBack.setOnClickListener {
            // Redirect to SettingActivity
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}
