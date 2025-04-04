package com.vayo.fitcheq

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

//Wil handle Authentication logic
class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var sharedPreferences: SharedPreferences? = null

    private val _authState = MutableStateFlow(auth.currentUser != null)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    private val _isProfileCompleted = MutableStateFlow<Boolean?>(null)
    val isProfileCompleted: StateFlow<Boolean?> = _isProfileCompleted.asStateFlow()

    private val _userGender = MutableStateFlow<String?>(null)
    val userGender: StateFlow<String?> = _userGender.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Add these to your AuthViewModel
    private val _isCheckingProfile = MutableStateFlow(false)
    val isCheckingProfile: StateFlow<Boolean> = _isCheckingProfile.asStateFlow()

    fun initializeSharedPreferences(context: Context) {
        Log.d("NavigationDebug", "Initializing SharedPreferences")
        sharedPreferences = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        Log.d("NavigationDebug", "SharedPreferences initialized successfully")
        
        // Check profile after SharedPreferences is initialized
        if (auth.currentUser != null) {
            Log.d("NavigationDebug", "User is logged in, checking profile...")
            checkUserProfile()
        } else {
            Log.d("NavigationDebug", "No user logged in, skipping profile check")
        }
    }

    private fun saveProfileToSharedPreferences(profileCompleted: Boolean, gender: String?) {
        Log.d("NavigationDebug", "Saving to SharedPreferences - profileCompleted: $profileCompleted, gender: $gender")
        sharedPreferences?.edit()?.apply {
            putBoolean("profile_completed", profileCompleted)
            putString("user_gender", gender)
            apply()
        }
        Log.d("NavigationDebug", "SharedPreferences saved successfully")
    }

    private fun loadProfileFromSharedPreferences(): Pair<Boolean, String?> {
        val profileCompleted = sharedPreferences?.getBoolean("profile_completed", false) ?: false
        val gender = sharedPreferences?.getString("user_gender", null)
        Log.d("NavigationDebug", "Loading from SharedPreferences - profileCompleted: $profileCompleted, gender: $gender")
        return Pair(profileCompleted, gender)
    }

    init {
        Log.d("NavigationDebug", "AuthViewModel initialized with currentUser: ${auth.currentUser?.uid}")
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun signUp(email: String, password: String, navController: NavController) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = true
                    showToast("Registration Successful!")
                } else {
                    showToast(task.exception?.localizedMessage ?: "Registration failed")
                }
            }
    }

    fun login(email: String, password: String) {
        Log.d("NavigationDebug", "=== Login Process Started ===")
        Log.d("NavigationDebug", "Login attempt with email: $email")
        
        if (email.isBlank() || password.isBlank()) {
            showToast("Email and password cannot be empty")
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d("NavigationDebug", "Starting Firebase authentication...")
                val result = auth.signInWithEmailAndPassword(email, password).await()
                
                Log.d("NavigationDebug", "Firebase auth successful, updating states...")
                _authState.value = true
                _isProfileCompleted.value = null
                _userGender.value = null
                
                Log.d("NavigationDebug", """
                    States after login:
                    authState: ${_authState.value}
                    isProfileCompleted: ${_isProfileCompleted.value}
                    userGender: ${_userGender.value}
                """.trimIndent())
                
                showToast("Login Successful!")
                
                // Check user profile after successful login
                checkUserProfile()
                
            } catch (e: Exception) {
                Log.e("NavigationDebug", "Login process failed", e)
                showToast(e.localizedMessage ?: "Login failed")
            }
        }
    }

    fun logout() {
        Log.d("NavigationDebug", "=== Logout Started ===")
        viewModelScope.launch {
            auth.signOut()
            _authState.value = false
            _isProfileCompleted.value = null
            _userGender.value = null
            // Clear SharedPreferences on logout
            sharedPreferences?.edit()?.clear()?.apply()
            Log.d("NavigationDebug", "Logout completed, all states reset and SharedPreferences cleared")
            showToast("Logged out successfully!")
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun checkUserProfile() {
        val userId = auth.currentUser?.uid
        Log.d("NavigationDebug", "=== Profile Check Started ===")
        Log.d("NavigationDebug", "Checking profile for userId: $userId")
        
        if (userId == null) {
            Log.d("NavigationDebug", "No user ID found, setting profile states to false/null")
            _isProfileCompleted.value = false
            _userGender.value = null
            return
        }

        if (sharedPreferences == null) {
            Log.e("NavigationDebug", "SharedPreferences not initialized!")
            return
        }

        viewModelScope.launch {
            _isCheckingProfile.value = true
            Log.d("NavigationDebug", "Profile check started, isCheckingProfile set to true")
            
            try {
                // First check SharedPreferences
                val (localProfileCompleted, localGender) = loadProfileFromSharedPreferences()
                
                if (localProfileCompleted && localGender != null) {
                    Log.d("NavigationDebug", "Found valid profile data in SharedPreferences")
                    _isProfileCompleted.value = true
                    _userGender.value = localGender
                    _isCheckingProfile.value = false
                    return@launch
                }

                // If SharedPreferences data is incomplete or missing, fetch from Firestore
                Log.d("NavigationDebug", "Fetching user document from Firestore...")
                val document = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                val exists = document.exists()
                val completed = document.getBoolean("profileCompleted") ?: false
                val gender = document.getString("gender")

                Log.d("NavigationDebug", """
                    Profile check results:
                    Document exists: $exists
                    Profile completed: $completed
                    Gender: $gender
                """.trimIndent())

                if (exists && completed && gender != null) {
                    Log.d("NavigationDebug", "Found valid profile in Firestore, updating SharedPreferences")
                    _isProfileCompleted.value = true
                    _userGender.value = gender
                    saveProfileToSharedPreferences(true, gender)
                    Log.d("NavigationDebug", "Profile data saved to SharedPreferences")
                } else {
                    Log.d("NavigationDebug", "Profile incomplete or missing in Firestore")
                    _isProfileCompleted.value = false
                    _userGender.value = null
                }
                
                Log.d("NavigationDebug", """
                    Updated profile states:
                    isProfileCompleted: ${_isProfileCompleted.value}
                    userGender: ${_userGender.value}
                """.trimIndent())
            } catch (e: Exception) {
                Log.e("NavigationDebug", "Error checking profile", e)
                _isProfileCompleted.value = false
                _userGender.value = null
                showToast("Error checking profile: ${e.localizedMessage}")
            } finally {
                _isCheckingProfile.value = false
                Log.d("NavigationDebug", "Profile check completed, isCheckingProfile set to false")
            }
        }
    }
}