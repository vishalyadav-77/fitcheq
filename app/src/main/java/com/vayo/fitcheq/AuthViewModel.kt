package com.vayo.fitcheq

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow

import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

//Wil handle Authentication logic
class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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

    init {
        Log.d("NavigationDebug", "AuthViewModel initialized with currentUser: ${auth.currentUser?.uid}")
        // Check profile if user is logged in
        if (auth.currentUser != null) {
            checkUserProfile()
        }
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
                
                // Immediately check user profile after successful login
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
            Log.d("NavigationDebug", "Logout completed, all states reset")
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

        viewModelScope.launch {
            _isCheckingProfile.value = true
            Log.d("NavigationDebug", "Profile check started, isCheckingProfile set to true")
            
            try {
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

                _isProfileCompleted.value = exists && completed
                _userGender.value = gender
                
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