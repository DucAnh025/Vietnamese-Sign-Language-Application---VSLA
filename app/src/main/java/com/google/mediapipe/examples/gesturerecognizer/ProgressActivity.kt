package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityProgressBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private lateinit var videoPieChartView: PieChartView
    private lateinit var quizPieChartView: PieChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoPieChartView = binding.videoPieChartView
        quizPieChartView = binding.quizPieChartView

        fetchProgressData()
        fetchQuizProgressData()

        // Setup Back Button
        // Setup Back Button
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Setup Favorite Videos Button
        binding.btnFavoritevideos.setOnClickListener {
            val intent = Intent(this, FavoriteVideosActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchProgressData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/video/progress")
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProgressActivity", "API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    try {
                        if (!jsonData.isNullOrEmpty()) {
                            val jsonObject = JSONObject(jsonData)
                            val learned = jsonObject.getInt("learned")
                            val total = jsonObject.getInt("total")

                            runOnUiThread {
                                videoPieChartView.setData(learned, total, "Video Progress")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ProgressActivity", "JSON parsing error: ${e.message}")
                    }
                } else {
                    Log.e("ProgressActivity", "API request unsuccessful: ${response.code}")
                }
            }
        })
    }

    private fun fetchQuizProgressData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/quiz/progress")
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProgressActivity", "Quiz API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    try {
                        if (!jsonData.isNullOrEmpty()) {
                            val jsonObject = JSONObject(jsonData)
                            val quizDone = jsonObject.getInt("quiz_done")
                            val totalQuiz = jsonObject.getInt("total_quiz")

                            runOnUiThread {
                                quizPieChartView.setData(quizDone, totalQuiz, "Quiz Progress")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ProgressActivity", "JSON parsing error: ${e.message}")
                    }
                } else {
                    Log.e("ProgressActivity", "API request unsuccessful: ${response.code}")
                }
            }
        })
    }
}
