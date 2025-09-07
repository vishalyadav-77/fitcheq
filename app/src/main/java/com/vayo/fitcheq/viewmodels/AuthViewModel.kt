package com.vayo.fitcheq.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
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


    val userProfile = mutableStateOf<UserProfile?>(null)


    fun initializeSharedPreferences(context: Context) {
        if (sharedPreferences == null) {
            Log.d("NavigationDebug", "Initializing SharedPreferences")
            sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            Log.d("NavigationDebug", "SharedPreferences initialized successfully")
        }

        // Check profile after SharedPreferences is initialized
        if (auth.currentUser != null) {
            Log.d("NavigationDebug", "User is logged in, checking profile...")
            checkUserProfile(context)
        } else {
            Log.d("NavigationDebug", "No user logged in, skipping profile check")
        }
    }

    fun saveUserProfileToSharedPreferences(context: Context, userProfile: UserProfile) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()

        val json = gson.toJson(userProfile)
        editor.putString("user_profile", json)

        // Clear old keys to prevent conflicts
        editor.remove("uId")
        editor.remove("name")
        editor.remove("gender")
        editor.remove("occupation")
        editor.remove("ageGroup")
        editor.remove("preferPlatform")
        editor.remove("profileCompleted")
        editor.remove("height")
        editor.remove("bodyType")

        editor.apply()
    }

    fun loadUserProfileFromSharedPreferences(context: Context): UserProfile {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // 1. Try JSON version
        val json = prefs.getString("user_profile", null)
        if (json != null) {
            val profile = gson.fromJson(json, UserProfile::class.java)
            Log.d("UserProfile", "Using migrated UserProfile ✅")
            return profile
        }

        // 2. Fallback: old style
        val gender = prefs.getString("gender", "") ?: ""
        val occupation = prefs.getString("occupation", "") ?: ""
        val ageGroup = AgeGroup.valueOf(prefs.getString("ageGroup", "UNSPECIFIED") ?: "UNSPECIFIED")
        val preferPlatform = PreferPlatform.valueOf(prefs.getString("preferPlatform", PreferPlatform.moderate.name) ?: PreferPlatform.moderate.name)
        val profileCompleted = prefs.getBoolean("profileCompleted", false)
        val height = try {
            HeightGroup.valueOf(prefs.getString("height", HeightGroup.average.name) ?: HeightGroup.average.name)
        } catch (e: Exception) {
            HeightGroup.average
        }
        val bodyType = try {
            BodyType.valueOf(prefs.getString("bodyType", BodyType.average.name) ?: BodyType.average.name)
        } catch (e: Exception) {
            BodyType.average
        }

        val oldProfile = UserProfile(
            uId = prefs.getString("uId", "") ?: "",
            name = prefs.getString("name", "") ?: "",
            gender = gender,
            occupation = occupation,
            ageGroup = ageGroup,
            preferPlatform = preferPlatform,
            profileCompleted = profileCompleted,
            height = height,
            bodyType = bodyType
        )
        Log.d("UserProfile", "Old keys detected: ${prefs.all.keys}")

        // 3. Auto-migrate
        saveUserProfileToSharedPreferences(context, oldProfile)
        Log.d("UserProfile", "Migrated old SharedPrefs to JSON ✅")

        return oldProfile
    }

    init {
        Log.d("NavigationDebug", "AuthViewModel initialized with currentUser: ${auth.currentUser?.uid}")
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun signUp(email: String, password: String, navController: NavController) {
        Log.d("NavigationDebug", "=== SignUp Process Started ===")
        Log.d("NavigationDebug", "SignUp attempt with email: $email")

        if (email.isBlank() || password.isBlank()) {
            showToast("Email and password cannot be empty")
            return
        }

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

                Log.d("NavigationDebug", "User document created successfully")

            } catch (e: Exception) {
                Log.e("NavigationDebug", "SignUp process failed", e)
                showToast(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String,context: Context) {
        Log.d("NavigationDebug", "=== Login Process Started ===")
        Log.d("NavigationDebug", "Login attempt with email: $email")

        if (email.isBlank() || password.isBlank()) {
            showToast("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                _isCheckingProfile.value = true
                Log.d("NavigationDebug", "Starting Firebase authentication...")
                val result = auth.signInWithEmailAndPassword(email, password).await()

                Log.d("NavigationDebug", "Firebase auth successful, updating states...")
                _authState.value = true
                _isProfileCompleted.value = null
                _userGender.value = null

                _currentUserId.value = auth.currentUser?.uid

                Log.d("NavigationDebug", """
                    States after login:
                    authState: ${_authState.value}
                    isProfileCompleted: ${_isProfileCompleted.value}
                    userGender: ${_userGender.value}
                """.trimIndent())

                showToast("Login Successful!")

                // Check user profile after successful login
                checkUserProfile(context)

            } catch (e: Exception) {
                Log.e("NavigationDebug", "Login process failed", e)
                _isCheckingProfile.value = false
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
            _currentUserId.value = null
            sharedPreferences?.edit()?.clear()?.apply()
            
            // Clear favorites when logging out
            val maleHomeViewModel = MaleHomeViewModel()
            maleHomeViewModel.clearFavorites()
            Log.d("UserProfile", "SharedPrefs cleared? keys=${sharedPreferences?.all}")
            Log.d("NavigationDebug", "Logout completed, all states reset and SharedPreferences cleared")
            showToast("Logged out successfully!")
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun checkUserProfile(context: Context) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProfileCheckTime < PROFILE_CHECK_DEBOUNCE) {
            Log.d("NavigationDebug", "Skipping profile check due to debounce")
            return
        }
        lastProfileCheckTime = currentTime

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
                val userProfile = loadUserProfileFromSharedPreferences(context)
                if (userProfile != null && userProfile.profileCompleted && userProfile.gender.isNotEmpty()) {
                    _isProfileCompleted.value = true
                    _userGender.value = userProfile.gender
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
                val uid = document.getString("uid")
                val name = document.getString("name")
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
                    val profile = UserProfile(
                        uId = uid ?: "",
                        name = name ?: "",
                        gender = gender ?: "",
                        occupation = occupation ?: "",
                        ageGroup = ageGroup,
                        preferPlatform = preferPlatform,
                        height = height,
                        bodyType = bodyType,
                        profileCompleted = completed
                    )
                    saveUserProfileToSharedPreferences(context, profile)

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