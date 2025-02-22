package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityWithNavBinding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWithNavBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var client: OkHttpClient
    private lateinit var tvFullName: TextView
    private lateinit var profileImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set content for the HomeActivity
        layoutInflater.inflate(R.layout.activity_main, binding.container, true)

        // Initialize OkHttpClient
        client = OkHttpClient()

        // Find and set up the Toolbar as the Support ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Enable the hamburger icon on the toolbar for opening and closing the drawer
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        // Set up NavigationView and get reference to header view
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val headerView: View = navigationView.getHeaderView(0)
        tvFullName = headerView.findViewById(R.id.tvFullName)
        profileImage = headerView.findViewById(R.id.profile_image)

        // Fetch user info when this activity loads
        fetchUserInfo()

        // Handle navigation item selections in the sidebar menu
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_edit_profile -> startActivity(Intent(this, EditProfileActivity::class.java))
                R.id.nav_notif -> startActivity(Intent(this, NotificationActivity::class.java))
                R.id.nav_progress -> startActivity(Intent(this, ProgressActivity::class.java))
                R.id.nav_watchedvideos -> startActivity(Intent(this, WatchedVideosActivity::class.java))
                R.id.nav_help -> startActivity(Intent(this, HelpAndSupportActivity::class.java))
                R.id.nav_logout -> {
                    val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("auth_token")
                    editor.apply()

                    // Redirect to LoginActivity
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Setup bottom navigation
        binding.bottomNavigationView.selectedItemId = R.id.home
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.planet -> {
                    startActivity(Intent(this, TaiLieuActivity::class.java))
                    true
                }
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

        // Set up button listeners for specific actions
        findViewById<Button>(R.id.btnViewDetail).setOnClickListener {
            startActivity(Intent(this, DetailFragment::class.java))
        }

        findViewById<ImageButton>(R.id.btnNotif_Main).setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        findViewById<Button>(R.id.btnSeeAll).setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
    }

    private fun fetchUserInfo() {
        // Retrieve token from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Build the request
        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/get_user_info")
            .get()
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        // Make asynchronous API call
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Failed to fetch user info", Toast.LENGTH_SHORT).show()
                }
                Log.e("UserInfoError", "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    try {
                        val jsonResponse = JSONObject(responseData ?: "")
                        val fullName = jsonResponse.getString("full_name")
                        val imageUrl = jsonResponse.getString("image_url")

                        // Update UI on the main thread
                        runOnUiThread {
                            tvFullName.text = fullName
                            Glide.with(this@MainActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.circle_background_01)
                                .error(R.drawable.circle_background_01)
                                .apply(RequestOptions.circleCropTransform())
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(profileImage)
                        }
                    } catch (e: JSONException) {
                        Log.e("UserInfoError", "JSON parsing error: ${e.message}")
                    }
                } else {
                    Log.e("UserInfoError", "Response unsuccessful: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Failed to load user info", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (drawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}
