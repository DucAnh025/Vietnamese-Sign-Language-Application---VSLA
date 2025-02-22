package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityWithNavBinding
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityFavoritevideosBinding
import com.google.mediapipe.examples.gesturerecognizer.databinding.ItemNewsBinding
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class FavoriteVideosActivity : FragmentActivity() {

    private lateinit var binding: ActivityWithNavBinding
    private lateinit var favoriteVideosBinding: ActivityFavoritevideosBinding
    private lateinit var adapter: VideosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWithNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inflate layout cho Watched Videos vào container của ActivityWithNav
        favoriteVideosBinding = ActivityFavoritevideosBinding.inflate(layoutInflater, binding.container, true)

        favoriteVideosBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = VideosAdapter()
        favoriteVideosBinding.recyclerView.adapter = adapter

        fetchVideoData()

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
        favoriteVideosBinding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun fetchVideoData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/video/favorite/list")
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("WatchedVideosActivity", "API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
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
                            adapter.setVideoItems(videoItems)
                        }
                    } catch (e: JSONException) {
                        Log.e("WatchedVideosActivity", "JSON parsing error: ${e.message}")
                    }
                } else {
                    Log.e("WatchedVideosActivity", "API request unsuccessful: ${response.code}")
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

    private inner class VideosAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<VideoViewHolder>() {
        private val videoItems = mutableListOf<VideoItem>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
            val itemBinding = ItemNewsBinding.inflate(layoutInflater, parent, false)
            return VideoViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
            holder.bind(videoItems[position])
        }

        override fun getItemCount(): Int = videoItems.size

        fun setVideoItems(items: List<VideoItem>) {
            videoItems.clear()
            videoItems.addAll(items)
            notifyDataSetChanged()
        }
    }

    private inner class VideoViewHolder(
        private val itemBinding: ItemNewsBinding
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemBinding.root) {

        private var videoUrl: String? = null

        fun bind(videoItem: VideoItem) {
            itemBinding.titleTextView.text = videoItem.videoName
            itemBinding.dateTextView.text = "Year: ${videoItem.year}"
            itemBinding.locationTextView.text = videoItem.location
            Glide.with(this@FavoriteVideosActivity)
                .load(videoItem.thumbImage)
                .into(itemBinding.imageView)
            videoUrl = videoItem.videoUrl

            itemView.setOnClickListener {
                videoUrl?.let {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                }
            }
        }
    }
}
