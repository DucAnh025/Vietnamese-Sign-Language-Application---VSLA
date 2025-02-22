package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivitySearchBinding
import com.google.mediapipe.examples.gesturerecognizer.databinding.ItemNewsBinding
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class SearchActivity : FragmentActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter()
        binding.searchResultsRecyclerView.adapter = searchAdapter

        // Get keyword from intent and perform search
        val keyword = intent.getStringExtra("search_keyword") ?: ""
        if (keyword.isNotEmpty()) {
            searchVideos(keyword)
        }
    }

    private fun searchVideos(keyword: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Log.e("SearchActivity", "No authentication token found")
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/search_video?keyword=$keyword")
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SearchActivity", "API request failed: ${e.message}")
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
                            videoItems.add(VideoItem(id, location, tag, thumbImage, videoName, videoUrl, year))
                        }
                        runOnUiThread {
                            searchAdapter.setVideoItems(videoItems)
                        }
                    } catch (e: JSONException) {
                        Log.e("SearchActivity", "JSON parsing error: ${e.message}")
                    }
                } else {
                    val errorBody = response.body?.string()
                    Log.e("SearchActivity", "API request unsuccessful: ${response.code} - $errorBody")
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
        val year: Int
    )

    private inner class SearchAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<SearchViewHolder>() {
        private val videoItems = mutableListOf<VideoItem>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
            val itemBinding = ItemNewsBinding.inflate(
                layoutInflater, parent, false
            )
            return SearchViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
            holder.bind(videoItems[position])
        }

        override fun getItemCount(): Int = videoItems.size

        fun setVideoItems(items: List<VideoItem>) {
            videoItems.clear()
            videoItems.addAll(items)
            notifyDataSetChanged()
        }
    }

    private inner class SearchViewHolder(
        private val itemBinding: ItemNewsBinding
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemBinding.root) {

        private var videoUrl: String? = null

        fun bind(videoItem: VideoItem) {
            itemBinding.titleTextView.text = videoItem.videoName
            itemBinding.dateTextView.text = "Year: ${videoItem.year}"
            itemBinding.locationTextView.text = videoItem.location
            Glide.with(this@SearchActivity)
                .load(videoItem.thumbImage)
                .into(itemBinding.imageView)
            videoUrl = videoItem.videoUrl

            itemView.setOnClickListener {
                videoUrl?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    startActivity(intent)
                }
            }
        }
    }
}
