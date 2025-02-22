package com.google.mediapipe.examples.gesturerecognizer

data class User(
    var fullName: String? = null, // Optional field for sign-up
    var email: String,
    var password: String
) {
    // Secondary constructor for login (email, password)
    constructor(email: String, password: String) : this(null, email, password)
}
