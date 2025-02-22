package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityQuizzesBinding
import com.google.mediapipe.examples.gesturerecognizer.databinding.ItemQuizzesBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class QuizzesActivity : FragmentActivity() {

    private lateinit var binding: ActivityQuizzesBinding
    private lateinit var quizzesAdapter: QuizzesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityQuizzesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        quizzesAdapter = QuizzesAdapter()
        binding.recyclerView.adapter = quizzesAdapter

        // Fetch quiz data
        fetchQuizData()
        // Setup Back Button
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, TaiLieuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchQuizData() {
        // Get auth token from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/quiz/bang_chu_cai")
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("QuizzesActivity", "API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    try {
                        val jsonArray = JSONArray(jsonData)
                        val quizItems = mutableListOf<QuizItem>()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val question = jsonObject.getString("question")
                            val thumbnail = jsonObject.getString("thumbnail")
                            val choices = jsonObject.getJSONArray("choices")
                            val correctAnswer = jsonObject.getString("correct_answer")
                            val answeredCorrectly = jsonObject.getBoolean("answered_correctly")
                            val videoId = jsonObject.getString("video_id")
                            val choicesList = mutableListOf<String>()
                            for (j in 0 until choices.length()) {
                                choicesList.add(choices.getString(j))
                            }

                            quizItems.add(
                                QuizItem(
                                    question = question,
                                    thumbnail = thumbnail,
                                    choices = choicesList,
                                    correctAnswer = correctAnswer,
                                    answeredCorrectly = answeredCorrectly,
                                    videoId = videoId
                                )
                            )
                        }
                        runOnUiThread {
                            quizzesAdapter.setQuizItems(quizItems)
                        }
                    } catch (e: JSONException) {
                        Log.e("QuizzesActivity", "JSON parsing error: ${e.message}")
                    }
                } else {
                    Log.e("QuizzesActivity", "API request unsuccessful: ${response.code}")
                }
            }
        })
    }

    data class QuizItem(
        val question: String,
        val thumbnail: String,
        val choices: List<String>,
        val correctAnswer: String,
        val answeredCorrectly: Boolean,
        val videoId: String
    )

    private inner class QuizzesAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<QuizzesViewHolder>() {
        private val quizItems = mutableListOf<QuizItem>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizzesViewHolder {
            val itemBinding = ItemQuizzesBinding.inflate(layoutInflater, parent, false)
            return QuizzesViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: QuizzesViewHolder, position: Int) {
            holder.bind(quizItems[position])
        }

        override fun getItemCount(): Int = quizItems.size

        fun setQuizItems(items: List<QuizItem>) {
            quizItems.clear()
            quizItems.addAll(items)
            notifyDataSetChanged()
        }
    }

    private inner class QuizzesViewHolder(
        private val itemBinding: ItemQuizzesBinding
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(quizItem: QuizItem) {
            itemBinding.questionText.text = quizItem.question

            // Load thumbnail image
            Glide.with(this@QuizzesActivity)
                .load(quizItem.thumbnail)
                .into(itemBinding.questionImage)

            // Dynamically set the choices in the RadioGroup
            itemBinding.answerGroup.removeAllViews()
            var correctRadioButton: RadioButton? = null
            for (choice in quizItem.choices) {
                val radioButton = RadioButton(this@QuizzesActivity).apply {
                    text = choice
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black))
                }
                itemBinding.answerGroup.addView(radioButton)
                if (choice == quizItem.correctAnswer) {
                    correctRadioButton = radioButton
                }
            }

            if (quizItem.answeredCorrectly && correctRadioButton != null) {
                correctRadioButton.isChecked = true
                correctRadioButton.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            }

            itemBinding.answerGroup.setOnCheckedChangeListener { group, checkedId ->
                val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                if (selectedRadioButton != null) {
                    val selectedAnswer = selectedRadioButton.text.toString()
                    if (selectedAnswer == quizItem.correctAnswer) {
                        // Correct answer selected
                        selectedRadioButton.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                        Toast.makeText(this@QuizzesActivity, "Correct!", Toast.LENGTH_SHORT).show()

                        // Save result in database (send to API)
                        submitQuizResult(quizItem.videoId)
                    } else {
                        // Wrong answer
                        Toast.makeText(this@QuizzesActivity, "Wrong! Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun submitQuizResult(videoId: String) {
            val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val authToken = sharedPreferences.getString("auth_token", null)

            if (authToken == null) {
                Toast.makeText(this@QuizzesActivity, "User not authenticated", Toast.LENGTH_SHORT).show()
                return
            }

            val client = OkHttpClient()
            val jsonObject = JSONObject().apply {
                put("video_id", videoId)
            }
            val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url("https://boxgateway.kozow.com/quiz/passed")
                .addHeader("Authorization", "Bearer $authToken")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("QuizzesActivity", "Failed to submit quiz result: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        Log.e("QuizzesActivity", "Quiz result submission failed: ${response.code}")
                    }
                }
            })
        }
    }
}
