package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentActivity
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityWithNavBinding

class TaiLieuActivity : FragmentActivity() {

    companion object {
        private const val TAG = "TaiLieuActivity"
    }

    private lateinit var binding: ActivityWithNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutInflater.inflate(R.layout.activity_tailieu, binding.container, true)

        // Setup bottom navigation
        binding.bottomNavigationView.selectedItemId = R.id.home
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

        // Setup search event
        setupSearchEvent()

        // Setup separate listeners for each Explore button
        setupExploreButtons()
    }

    private fun setupSearchEvent() {
        val searchEditText = findViewById<android.widget.EditText>(R.id.etSearch)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val keyword = searchEditText.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    val intent = Intent(this, SearchActivity::class.java)
                    intent.putExtra("search_keyword", keyword)
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupExploreButtons() {
        // Button 1: Navigate to 1Activity
        findViewById<View>(R.id.ExploreButton01)?.setOnClickListener {
            navigateToActivity(NewsActivity::class.java)
        }

        // Button 2: Navigate to 2Activity
        findViewById<View>(R.id.ExploreButton02)?.setOnClickListener {
            navigateToActivity(TaiLieuSoActivity::class.java)
        }

        // Button 3: Navigate to 3Activity
        findViewById<View>(R.id.ExploreButton03)?.setOnClickListener {
            navigateToActivity(TaiLieuDauActivity::class.java)
        }

        // Button 4: Navigate to 4Activity
        findViewById<View>(R.id.ExploreButton04)?.setOnClickListener {
            navigateToActivity(NewsActivity::class.java)
        }

        // Button 5: Navigate to 5Activity
        findViewById<View>(R.id.ExploreButton05)?.setOnClickListener {
            navigateToActivity(TaiLieuDauActivity::class.java)
        }

        // Button 6: Navigate to 6Activity
        findViewById<View>(R.id.ExploreButton06)?.setOnClickListener {
            navigateToActivity(TaiLieuSoActivity::class.java)
        }

        // Button 7: Navigate to 7Activity
        findViewById<View>(R.id.ExploreButton07)?.setOnClickListener {
            navigateToActivity(NewsActivity::class.java)
        }

        // Button Star: Navigate to StarActivity
        findViewById<View>(R.id.btn_favoritevideos)?.setOnClickListener {
            navigateToActivity(FavoriteVideosActivity::class.java)
        }
        // Button Star: Navigate to StarActivity
        findViewById<View>(R.id.btn_progress)?.setOnClickListener {
            navigateToActivity(ProgressActivity::class.java)
        }
        // Button Star: Navigate to StarActivity
        findViewById<View>(R.id.btn_quizz)?.setOnClickListener {
            navigateToActivity(QuizzesActivity::class.java)
        }
    }
    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)

    }
}
