package com.google.mediapipe.examples.gesturerecognizer

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class HelpAndSupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_and_support)

        val btnBack: ImageButton = findViewById(R.id.backButton)
        btnBack.setOnClickListener {
            // Close the activity and go back to the previous screen
            finish()
        }
    }
}
