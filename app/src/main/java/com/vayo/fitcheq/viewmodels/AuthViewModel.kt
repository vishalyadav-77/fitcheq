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
            sharedPreferences = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            Log.d("NavigationDebug", "SharedPreferences initialized successfully")
        }

        // Check profile after SharedPreferences is initialized
        if (auth.currentUser != null) {
            Log.d("NavigationDebug", "User is logged in, checking profile...")
            checkUserProfile()
        } else {
            Log.d("NavigationDebug", "No user logged in, skipping profile check")
        }
    }

    private fun saveProfileToSharedPreferences(profileCompleted: Boolean,
                                               gender: String?,
                                               name: String?,
                                               ageGroup: AgeGroup?,
                                               occupation: String?,
                                               preferPlatform: PreferPlatform?,
                                               height: HeightGroup?,
                                               bodyType: BodyType?,
                                               uid: String?) {
        Log.d("NavigationDebug", "Saving to SharedPreferences - profileCompleted: $profileCompleted, gender: $gender")
        Log.d("NavigationDebug", "Saving to SharedPreferences - age Group: $ageGroup")
        sharedPreferences?.edit()?.apply {
            putBoolean("profile_completed", profileCompleted)
            putString("user_gender", gender)
            putString("user_name", name)
            putString("user_ageGroup", ageGroup?.name)
            putString("user_occupation", occupation)
            putString("user_preferPlatform", preferPlatform?.name)
            putString("user_heightGroup", height?.name)
            putString("user_bodyType", bodyType?.name)
            apply()
        }
        Log.d("NavigationDebug", "SharedPreferences saved successfully")
    }
    fun loadProfile(): UserProfile { // Renamed for clarity
        return loadProfileFromSharedPreferences()
    }


     fun loadProfileFromSharedPreferences(): UserProfile {
        return UserProfile(
            uId = sharedPreferences?.getString("user_uId", "") ?: "",
            name = sharedPreferences?.getString("user_name", "") ?: "",
            gender = sharedPreferences?.getString("user_gender", "") ?: "",
            occupation = sharedPreferences?.getString("user_occupation", "") ?: "",
            ageGroup = sharedPreferences?.getString("user_ageGroup", null)
                ?.let { stringValue ->
                    runCatching { AgeGroup.valueOf(stringValue) }.getOrNull()
                } ?: AgeGroup.UNSPECIFIED,
            preferPlatform = sharedPreferences?.getString("user_preferPlatform", null)
                ?.let { stringValue ->
                    runCatching { PreferPlatform.valueOf(stringValue) }.getOrNull()
                } ?: PreferPlatform.moderate,
            profileCompleted = sharedPreferences?.getBoolean("profile_completed", false) ?: false,
            height = sharedPreferences?.getString("user_heightGroup", null)
                ?.let { stringValue ->
                    runCatching { HeightGroup.valueOf(stringValue) }.getOrNull()
                } ?: HeightGroup.average,
            bodyType = sharedPreferences?.getString("user_bodyType", null)
                ?.let { stringValue ->
                    runCatching { BodyType.valueOf(stringValue) }.getOrNull()
                } ?: BodyType.average
        ).also {
            Log.d("ProfileLoad", "Loaded profile: $it")
        }
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

    fun login(email: String, password: String) {
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
                checkUserProfile()

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
            
            Log.d("NavigationDebug", "Logout completed, all states reset and SharedPreferences cleared")
            showToast("Logged out successfully!")
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun checkUserProfile() {
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
                val userprofile = loadProfileFromSharedPreferences()
                val localProfileCompleted = userprofile.profileCompleted
                val localGender = userprofile.gender
//                val (localProfileCompleted, localGender) = loadProfileFromSharedPreferences()

                if (localProfileCompleted && localGender.isNotEmpty()) {
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
                    saveProfileToSharedPreferences(true, gender,name,ageGroup,occupation,preferPlatform,height,bodyType,uid)
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