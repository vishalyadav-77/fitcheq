package com.vayo.fitcheq.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.vayo.fitcheq.data.model.AgeGroup
import com.vayo.fitcheq.data.model.BodyType
import com.vayo.fitcheq.data.model.HeightGroup
import com.vayo.fitcheq.data.model.PreferPlatform
import com.vayo.fitcheq.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private var lastProfileCheckTime = 0L
    private val PROFILE_CHECK_DEBOUNCE = 1000L // 1 second

    private val _currentUserId = MutableStateFlow(auth.currentUser?.uid)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    fun initializeSharedPreferences(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        }

        // Check profile after SharedPreferences is initialized
        if (auth.currentUser != null) {
        //    Log.d("NavigationDebug", "User is logged in, checking profile...")
            checkUserProfile(context)
        } else {
      //      Log.d("NavigationDebug", "No user logged in, skipping profile check")
        }
    }

    fun saveUserProfileToSharedPreferences(context: Context, userProfile: UserProfile) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()

        val json = gson.toJson(userProfile)
        editor.putString("user_profile", json)
        editor.apply()
    }

    fun loadUserProfileFromSharedPreferences(context: Context): UserProfile? {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // 1. JSON version
        val json = prefs.getString("user_profile", null)
        return if (json != null) {
            val profile = gson.fromJson(json, UserProfile::class.java)
            profile
        } else {
            null
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun signUp(email: String, password: String, navController: NavController) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = true
                showToast("Registration Successful!")

                // Create initial user document in Firestore
                val userData = hashMapOf(
                    "profileCompleted" to false,
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(result.user?.uid ?: "")
                    .set(userData)
                    .await()

            } catch (e: Exception) {
                showToast(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String,context: Context) {
        viewModelScope.launch {
            try {
                _isCheckingProfile.value = true
                val result = auth.signInWithEmailAndPassword(email, password).await()

                _authState.value = true
                _isProfileCompleted.value = null
                _userGender.value = null

                _currentUserId.value = auth.currentUser?.uid

                showToast("Login Successful!")

                // Check user profile after successful login
                checkUserProfile(context)

            } catch (e: Exception) {
                _isCheckingProfile.value = false
                showToast(e.localizedMessage ?: "Login failed")
            }
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit){
        if (email.isBlank()) {
            onError("Email cannot be empty")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Failed to send password reset email")
            }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
            _authState.value = false
            _isProfileCompleted.value = null
            _userGender.value = null
            _currentUserId.value = null
            sharedPreferences?.edit()?.clear()?.apply()
            
            // Clear favorites when logging out
            val maleHomeViewModel = MaleHomeViewModel()
            maleHomeViewModel.clearFavorites()
            showToast("Logged out successfully!")
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun checkUserProfile(context: Context) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProfileCheckTime < PROFILE_CHECK_DEBOUNCE) {
            return
        }
        lastProfileCheckTime = currentTime

        val userId = auth.currentUser?.uid

        if (userId == null) {
            _isProfileCompleted.value = false
            _userGender.value = null
            return
        }

        if (sharedPreferences == null) {
            return
        }

        viewModelScope.launch {
            _isCheckingProfile.value = true

            try {
                // First check SharedPreferences
                val userProfile = loadUserProfileFromSharedPreferences(context)
                if (userProfile != null && userProfile.profileCompleted && userProfile.gender.isNotEmpty()) {
                    _isProfileCompleted.value = true
                    _userGender.value = userProfile.gender
                    _isCheckingProfile.value = false
                    return@launch
                }

                // If SharedPreferences data is incomplete or missing, fetch from Firestore
                val document = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                val exists = document.exists()
                val completed = document.getBoolean("profileCompleted") ?: false
                val gender = document.getString("gender")
                val uid = document.getString("uid")
                val name = document.getString("name")
                val phone = document.getString("phone")
                val occupation = document.getString("occupation")
                val ageGroupName = document.getString("ageGroup") ?: AgeGroup.UNSPECIFIED.name
                val preferPlatformName = document.getString("preferPlatform") ?: PreferPlatform.moderate.name
                val heightName = document.getString("height") ?: HeightGroup.average.name
                val bodyTypeName = document.getString("bodyType") ?: BodyType.average.name

                // Convert the string back to the enum instance
                val ageGroup = AgeGroup.valueOf(ageGroupName)
                val preferPlatform = PreferPlatform.valueOf(preferPlatformName)
                val height = HeightGroup.valueOf(heightName)
                val bodyType = BodyType.valueOf(bodyTypeName)

                if (exists && completed && gender != null) {
                    _isProfileCompleted.value = true
                    _userGender.value = gender
                    val profile = UserProfile(
                        uId = uid ?: "",
                        name = name ?: "",
                        phone = phone ?: "",
                        gender = gender ?: "",
                        occupation = occupation ?: "",
                        ageGroup = ageGroup,
                        preferPlatform = preferPlatform,
                        height = height,
                        bodyType = bodyType,
                        profileCompleted = completed
                    )
                    saveUserProfileToSharedPreferences(context, profile)

                } else {
                       _isProfileCompleted.value = false
                    _userGender.value = null
                }
            } catch (e: Exception) {
                _isProfileCompleted.value = false
                _userGender.value = null
                showToast("Error checking profile: ${e.localizedMessage}")
            } finally {
                _isCheckingProfile.value = false
            }
        }
    }
}