package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivitySignupBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var client: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)

        // Check for existing auth token before setting content view
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        // If auth token exists, redirect to MainActivity
        if (authToken != null) {
            val intent = Intent(this@SignupActivity, PracticeActivity::class.java)
            startActivity(intent)
            finish() // Close SignupActivity
            return
        }
        setContentView(binding.root)

        // Initialize OkHttpClient
        client = OkHttpClient()

        // Set up sign-up button click listener
        binding.btnSignUp.setOnClickListener { registerUser() }

        // Set up login text click listener to navigate to login screen
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser() {
        val fullName = binding.etFullName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Validate input
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 8 characters and contain one uppercase letter.", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Set API endpoint
        val url = "https://boxgateway.kozow.com/register"

        // Create JSON body for the request
        val requestBodyJson = JSONObject()
        try {
            requestBodyJson.put("full_name", fullName)
            requestBodyJson.put("email", email)
            requestBodyJson.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show()
            return
        }

        // Set JSON media type
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestBodyJson.toString().toRequestBody(mediaType)

        // Build the request
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        // Make asynchronous call
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SignupError", "Network request failed: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@SignupActivity,
                        "Network error. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    try {
                        val jsonResponse = JSONObject(responseData ?: "")
                        if (jsonResponse.has("status") && jsonResponse.getBoolean("status")) {
                            runOnUiThread {
                                Toast.makeText(this@SignupActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                                finish()
                            }
                        } else {
                            val errorMessage = jsonResponse.optString("message", "Registration failed")
                            runOnUiThread {
                                Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@SignupActivity, "Failed to parse response", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorBody = response.body?.string() ?: "Unknown error"
                    Log.e("SignupError", "Response unsuccessful: ${response.code} - $errorBody")
                    runOnUiThread {
                        Toast.makeText(this@SignupActivity, "Error: ${response.code}. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        val pattern = Pattern.compile(emailRegex)
        return pattern.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$"
        return password.matches(passwordRegex.toRegex())
    }
}
