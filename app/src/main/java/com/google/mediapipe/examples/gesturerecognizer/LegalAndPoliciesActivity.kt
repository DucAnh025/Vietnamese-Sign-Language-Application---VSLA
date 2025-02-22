package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class LegalAndPoliciesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_and_policies)

        val btnBack: ImageButton = findViewById(R.id.backButton)
        btnBack.setOnClickListener {
            val intent = Intent(this@LegalAndPoliciesActivity, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}
