package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityPracticeBinding

class PracticeActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityPracticeBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Setup NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        activityMainBinding.navigation.setupWithNavController(navController)
        activityMainBinding.navigation.setOnNavigationItemReselectedListener {
            // ignore the reselection
        }

        // Set OnClickListener for Back Button
        activityMainBinding.backButton.setOnClickListener {
            val intent = Intent(this, TaiLieuActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
