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
        if (email.isBlank() || password.isBlank()) {
            showToast("Email and password cannot be empty")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = true
                    showToast("Login Successful!")
                } else {
                    showToast(task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    fun logout() {
        Log.d("AUTH_DEBUG", "Logout function called")
        Log.d("AUTH_DEBUG", "Before logout - Current user: ${auth.currentUser?.uid}")
        auth.signOut()
        Log.d("AUTH_DEBUG", "After logout - Current user: ${auth.currentUser?.uid}")
        _authState.value = false
        _isProfileCompleted.value = null
        _userGender.value = null
        showToast("Logged out successfully!")
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun checkUserProfile() {
        val userId = auth.currentUser?.uid
        _isProfileCompleted.value = null
        _userGender.value = null

        if (userId != null) {
            viewModelScope.launch {
                try {
                    val document = firestore.collection("users")
                        .document(userId)
                        .get()
                        .await()

                    val exists = document.exists()
                    val completed = document.getBoolean("profileCompleted") ?: false
                    val gender = document.getString("gender")

                    _isProfileCompleted.value = exists && completed
                    _userGender.value = gender
                } catch (e: Exception) {
                    _isProfileCompleted.value = false
                    _userGender.value = null
                }
            }
        } else {
            _isProfileCompleted.value = false
            _userGender.value = null
        }
    }
}