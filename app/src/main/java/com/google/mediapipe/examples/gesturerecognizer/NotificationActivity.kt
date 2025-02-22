package com.google.mediapipe.examples.gesturerecognizer

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val btnBack: ImageButton = findViewById(R.id.backButton)
        btnBack.setOnClickListener {
            finish()
        }
    }
}
