# Vietnamese Sign Language Application (VSLA)

## 📱 Project Overview
**Vietnamese Sign Language Application (VSLA)** is an Android application designed to help users learn and practice Vietnamese Sign Language (VSL). It leverages AI-powered gesture recognition and interactive lessons to provide an intuitive and engaging learning experience. 

With a focus on accessibility and usability, **VSLA** integrates:
- **Real-time sign recognition** powered by **MediaPipe & TensorFlow Lite**.
- **Structured learning modules** with interactive quizzes, flashcards, and video lessons.
- **User progress tracking** for personalized learning experiences.

---

## ✨ Features
### **1. Authentication**
- User **registration, login, logout** with secure token storage.

### **2. User Profile Management**
- View, update personal information, and change passwords.

### **3. Learning & Practice**
- **Structured lessons**: Learn sign language by topics (alphabet, numbers, vocabulary).
- **Sign language search**: Quickly find sign meanings with video demonstrations.
- **Gesture recognition AI**:
  - **Real-time sign detection** using device camera.
  - **Upload & analyze images/videos** for sign recognition.
- **Flashcard mode** for vocabulary revision.
- **Quiz mode** to test and reinforce learning.

📌 *Learning with Real-time sign detection*

### **4. Progress Tracking**
- **Mark completed lessons** and **review learning history**.
- **Track learning milestones** via progress dashboard.

### **5. Optimization & Security**
- **HTTPS secure data transmission**.
- **Data caching** to improve performance.
- **Efficient API communication** using compressed JSON.

---

## 🏗 System Architecture
### **Tech Stack**
- **Frontend:** Android (Java/Kotlin)
- **Backend:** Flask API (Python)
- **Database:** PostgreSQL/MySQL
- **AI:** MediaPipe (Hand Tracking) + TensorFlow Lite
- **Deployment:** Amlogic TV Box Servers (Linux)

---

## 🚀 Installation & Setup

### **System Requirements**
- **Android 7.0+** (for the mobile app)
- **Python 3.8+** (for the backend)
- **PostgreSQL/MySQL** (for database)


### **📱 Android App Installation**
```bash
git clone https://github.com/DucAnh025/Vietnamese-Sign-Language-Application---VSLA

```

# 📖 Usage Guide

## Step 1: User Registration & Login
- New users can **create an account** using a valid email and password.
- Users can log in using their credentials, and tokens are stored for automatic re-login.

## Step 2: Exploring Learning Modules
- Navigate to the **learning section** to access structured lessons.
- Topics include **alphabet, numbers, common words, and expressions**.
- Each lesson includes **video demonstrations** for better visualization.

## Step 3: Practicing with AI-powered Gesture Recognition
- **Real-time practice**: Open the camera, perform a sign, and get instant feedback.
- **Upload feature**: Users can upload images or videos for sign recognition analysis.

## Step 4: Flashcards & Quiz Mode
- **Flashcards** help users memorize vocabulary effectively.
- **Quiz mode** evaluates user knowledge and provides scoring & feedback.

## Step 5: Tracking Learning Progress
- Users can **mark completed lessons** and monitor their progress on the dashboard.
- **Learning history** allows users to review past lessons and quizzes.

📌 *App interface*


## ❓ Troubleshooting & FAQ

### Q1: The app crashes when using the camera.
✔ Check if the app has **camera permissions** enabled in your Android settings.

### Q2: How can I update the AI model for sign recognition?
✔ Replace the **`.tflite`** file in the **`assets/model/`** directory with an updated model.

### Q3: The learning progress is not updating correctly.
✔ Ensure the app has **internet access** to sync with the backend server.

📌 **More FAQs**: [FAQ.md](https://github.com/DucAnh025)

## ☎️ Contact & Support

For any questions or support, feel free to reach out:

- 📧 **Email**: [buileducanh25@gmail.com](mailto:buileducanh25@gmail.com)  
- 💬 **Facebook**: [Duc Anh](https://www.facebook.com/ducanh.buile.56/)
- 🐦 **LinkedIn**: [Bui Le Duc Anh](www.linkedin.com/in/buileducanh2505)  

