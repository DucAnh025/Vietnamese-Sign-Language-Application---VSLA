package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ChangePasswordActivity : ComponentActivity() {

    private lateinit var client: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // Initialize OkHttpClient
        client = OkHttpClient()

        // Retrieve views from layout
        val oldPasswordEditText: EditText = findViewById(R.id.oldPassword)
        val newPasswordEditText: EditText = findViewById(R.id.newPassword)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmPassword)
        val btnSaveChangesPass: Button = findViewById(R.id.btn_save_changes)
        val btnBack: ImageButton = findViewById(R.id.backButton)

        // Set up click listener for save button
        btnSaveChangesPass.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Validate input
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            changePassword(oldPassword, newPassword)
        }

        // Set up click listener for back button
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        // Prepare JSON body
        val jsonObject = JSONObject()
        try {
            jsonObject.put("old_password", oldPassword)
            jsonObject.put("new_password", newPassword)
        } catch (e: JSONException) {
            e.printStackTrace()
            return
        }

        // Retrieve token from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Create request body with JSON content
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonObject.toString().toRequestBody(mediaType)

        // Build the request with PUT method and Authorization header
        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/change_password")
            .put(body)
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        // Make asynchronous API call
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "Connection error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Password changed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@ChangePasswordActivity, SettingActivity::class.java))
                        finish()
                    }
                } else {
                    val errorBody = response.body?.string() ?: "Unknown error"
                    runOnUiThread {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Error: ${response.code} - $errorBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
