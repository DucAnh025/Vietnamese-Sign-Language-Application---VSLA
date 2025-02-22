package com.google.mediapipe.examples.gesturerecognizer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var fullNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var mailEditText: EditText
    private var authToken: String? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openImagePicker()
            } else {
                Toast.makeText(this, "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show()
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    Glide.with(this)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(profileImage)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Retrieve token from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        profileImage = findViewById(R.id.profile_image)
        fullNameEditText = findViewById(R.id.et_full_name)
        usernameEditText = findViewById(R.id.et_username)
        phoneNumberEditText = findViewById(R.id.et_phone_number)
        mailEditText = findViewById(R.id.et_email)
        val backButton: ImageButton = findViewById(R.id.backButton)
        val saveChangesButton: Button = findViewById(R.id.btn_save_changes)

        // Fetch and display user info on create
        getUserInfo()

        profileImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                openImagePicker()
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        saveChangesButton.setOnClickListener {
            updateUserInfo()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun getUserInfo() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/get_user_info")
            .header("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity, "Failed to fetch user info", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            val json = JSONObject(responseBody)
                            val fullName = json.getString("full_name")
                            val username = json.getString("username")
                            val phoneNumber = json.getString("phone_number")
                            val imageUrl = json.getString("image_url")
                            val mail = json.getString("email")

                            runOnUiThread {
                                fullNameEditText.setText(fullName)
                                usernameEditText.setText(username)
                                phoneNumberEditText.setText(phoneNumber)
                                mailEditText.setText(mail)

                                Glide.with(this@EditProfileActivity)
                                    .load(imageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .error(R.drawable.ic_profile_placeholder)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(profileImage)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            runOnUiThread {
                                Toast.makeText(this@EditProfileActivity, "Failed to parse user info", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@EditProfileActivity, "Failed to fetch user info", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun updateUserInfo() {
        val fullName = fullNameEditText.text.toString()
        val username = usernameEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()

        val client = OkHttpClient()

        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("full_name", fullName)
            .addFormDataPart("username", username)
            .addFormDataPart("phone_number", phoneNumber)

        selectedImageUri?.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            val imageBytes = inputStream?.readBytes()
            if (imageBytes != null) {
                requestBodyBuilder.addFormDataPart(
                    "image", "profile.jpg",
                    imageBytes.toRequestBody("image/jpeg".toMediaType())
                )
            }
        }

        val requestBody = requestBodyBuilder.build()

        val request = Request.Builder()
            .url("https://boxgateway.kozow.com/change_user_info")
            .header("Authorization", "Bearer $authToken")
            .put(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity, "Failed to update user info", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditProfileActivity, "User info updated successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@EditProfileActivity, SettingActivity::class.java))
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Failed to update user info", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
