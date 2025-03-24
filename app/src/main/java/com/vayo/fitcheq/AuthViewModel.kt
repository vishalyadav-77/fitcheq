package com.vayo.fitcheq

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

//Wil handle Authentication logic
class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow(auth.currentUser != null)
     val authState: StateFlow<Boolean> = _authState.asStateFlow()

    private val _isProfileCompleted = MutableStateFlow<Boolean?>(null)
    val isProfileCompleted: StateFlow<Boolean?> = _isProfileCompleted.asStateFlow()



    private  val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = true
                    showToast("Registration Successful!") // ✅ Show success message
                } else {
                    showToast(task.exception?.localizedMessage ?: "Registration failed") // ✅ Show error
                }
            }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = true
                    showToast("Login Successful!") // ✅ Show success message
                } else {
                    showToast(task.exception?.localizedMessage ?: "Login failed") // ✅ Show error
                }
            }
    }

    fun logout() {
        Log.d("AuthViewModel", "Logout function called")
        auth.signOut()
        _authState.value = false
        showToast("Logged out successfully!") // ✅ Indicate logout

    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
    fun checkUserProfile(){
        val userId = auth.currentUser?.uid
        if (userId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val document = firestore.collection("users").document(userId).get().await()
                    _isProfileCompleted.value = document.exists() && document.getBoolean("profileCompleted") == true
                } catch (e: Exception) {
                    _isProfileCompleted.value = false
                }
            }
        } else {
            _isProfileCompleted.value = false
        }
    }
}