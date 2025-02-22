package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class DetailFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_detail)

        val btnBack: ImageButton = findViewById(R.id.backButton)
        btnBack.setOnClickListener {
            val intent = Intent(this@DetailFragment, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
