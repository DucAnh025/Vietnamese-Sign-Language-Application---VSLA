package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityNewsBinding
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityWithNavBinding
import com.google.mediapipe.examples.gesturerecognizer.databinding.ItemNewsBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class NewsActivity : FragmentActivity() {

    private lateinit var binding: ActivityWithNavBinding
    private lateinit var newsBinding: ActivityNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var client: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OkHttpClient
        client = OkHttpClient()

        // Inflate layout with nav
        binding = ActivityWithNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inflate news layout inside container
        newsBinding = ActivityNewsBinding.inflate(layoutInflater, binding.container, true)

        // Setup RecyclerView
        newsBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter()
        newsBinding.recyclerView.adapter = newsAdapter

        // Fetch video data
        fetchVideoData()

        // Setup bottom navigation
        binding.bottomNavigationView.selectedItemId = R.id.planet
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.planet -> true

                R.id.practice -> {
                    startActivity(Intent(this, PracticeActivity::class.java))
                    true
                }
                R.id.setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Setup Back Button
        newsBinding.backButton.setOnClickListener {
            val intent = Intent(this, TaiLieuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchVideoData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/video/bang_chu_cai")
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NewsActivity", "API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val jsonData = response.body?.string()
                        try {
                            val jsonArray = JSONArray(jsonData)
                            val videoItems = mutableListOf<VideoItem>()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val id = jsonObject.getInt("id")
                                val location = jsonObject.getString("location")
                                val tag = jsonObject.getString("tag")
                                val thumbImage = jsonObject.getString("thumb_image")
                                val videoName = jsonObject.getString("video_name")
                                val videoUrl = jsonObject.getString("video_url")
                                val year = jsonObject.getInt("year")
                                val favorite = jsonObject.getBoolean("favorite")
                                videoItems.add(VideoItem(id, location, tag, thumbImage, videoName, videoUrl, year, favorite))
                            }
                            runOnUiThread {
                                newsAdapter.setVideoItems(videoItems)
                            }
                        } catch (e: JSONException) {
                            Log.e("NewsActivity", "JSON parsing error: ${e.message}")
                        }
                    } else {
                        Log.e("NewsActivity", "API request unsuccessful: ${response.code}")
                    }
                }
            }
        })
    }

    private fun sendFavoriteVideoToServer(videoId: Int, add: Boolean, callback: (Boolean) -> Unit) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            callback(false)
            return
        }

        // Tạo JSON body cho cả POST và DELETE
        val jsonObject = JSONObject().apply {
            put("video_id", videoId)
        }
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = if (add) {
            // Gửi yêu cầu POST để thêm vào favorites
            Request.Builder()
                .url("https://boxgateway.kozow.com/video/favorite/add")
                .post(body)
                .addHeader("Authorization", "Bearer $authToken")
                .build()
        } else {
            // Gửi yêu cầu DELETE với body
            Request.Builder()
                .url("https://boxgateway.kozow.com/video/favorite/remove")
                .delete(body)  // Sử dụng body trong DELETE
                .addHeader("Authorization", "Bearer $authToken")
                .build()
        }

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@NewsActivity, "Failed to update favorites: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("NewsActivity", "Failed to update favorites: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        runOnUiThread {
                            val message = if (add) "Video added to favorites." else "Video removed from favorites."
                            Toast.makeText(this@NewsActivity, message, Toast.LENGTH_SHORT).show()
                        }
                        callback(true)
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@NewsActivity, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("NewsActivity", "Failed to update favorites: ${response.code}")
                        callback(false)
                    }
                }
            }
        })
    }
    private fun sendWatchedVideoToServer(videoId: Int) {
        val jsonObject = JSONObject().apply {
            put("video_id", videoId)
        }

        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonObject.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/video/watched/add")
            .post(body)
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@NewsActivity, "Failed to send watched video: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("NewsActivity", "Failed to send watched video: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@NewsActivity, "Watched video successfully sent to server.", Toast.LENGTH_SHORT).show()
                        }
                        Log.i("NewsActivity", "Watched video successfully sent to server.")
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@NewsActivity, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("NewsActivity", "Failed to send watched video: ${response.code}")
                    }
                }
            }
        })
    }


    data class VideoItem(
        val id: Int,
        val location: String,
        val tag: String,
        val thumbImage: String,
        val videoName: String,
        val videoUrl: String,
        val year: Int,
        val favorite: Boolean
    )

    private inner class NewsAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<NewsViewHolder>() {
        private val videoItems = mutableListOf<VideoItem>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val itemBinding = ItemNewsBinding.inflate(layoutInflater, parent, false)
            return NewsViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            holder.bind(videoItems[position])
        }

        override fun getItemCount(): Int = videoItems.size

        fun setVideoItems(items: List<VideoItem>) {
            videoItems.clear()
            videoItems.addAll(items)
            notifyDataSetChanged()
        }
    }

    private inner class NewsViewHolder(
        private val itemBinding: ItemNewsBinding
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemBinding.root) {

        private var videoUrl: String? = null
        private var videoId: Int? = null

        fun bind(videoItem: VideoItem) {
            var isFavorite: Boolean = videoItem.favorite
            itemBinding.titleTextView.text = videoItem.videoName
            itemBinding.dateTextView.text = "Year: ${videoItem.year}"
            itemBinding.locationTextView.text = videoItem.location
            Glide.with(this@NewsActivity)
                .load(videoItem.thumbImage)
                .into(itemBinding.imageView)

            videoUrl = videoItem.videoUrl
            videoId = videoItem.id
            if (videoItem.favorite) itemBinding.btnStar.setColorFilter(ContextCompat.getColor(itemView.context, R.color.mp_color_secondary))

            // Toggle favorite on button click
            itemBinding.btnStar.setOnClickListener {
                videoId?.let { id ->
                    sendFavoriteVideoToServer(id, !isFavorite) { success ->
                        if (success) {
                            isFavorite = !isFavorite
                            val color = if (isFavorite) R.color.mp_color_secondary else R.color.bottom_sheet_text_color
                            itemBinding.btnStar.setColorFilter(ContextCompat.getColor(itemView.context, color))
                        }
                    }
                }
            }

            itemView.setOnClickListener {
                videoUrl?.let { url ->
                    videoId?.let { id -> sendWatchedVideoToServer(id) }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
            }
        }
    }
}
