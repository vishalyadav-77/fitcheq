package com.vayo.fitcheq.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.vayo.fitcheq.data.model.OutfitData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MaleHomeViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _outfits = MutableStateFlow<List<OutfitData>>(emptyList())
    val outfits: StateFlow<List<OutfitData>> = _outfits
    
    private val _savedOutfits = MutableStateFlow<List<OutfitData>>(emptyList())
    val savedOutfits: StateFlow<List<OutfitData>> = _savedOutfits
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _favoriteMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val favoriteMap: StateFlow<Map<String, Boolean>> = _favoriteMap


    fun fetchOutfitsByTagAndGender(tag: String, gender: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Log.d("OutfitFetch", "Fetching outfits with tag: $tag and gender: $gender")
                
                val result = Firebase.firestore.collection("outfits")
                    .whereEqualTo("gender", gender)
                    .whereArrayContains("tags", tag)
                    .get()
                    .await()
                
                val outfitList = result.documents.mapNotNull { doc ->
                    doc.toObject(OutfitData::class.java)?.copy(id = doc.id)
                }
                
                Log.d("OutfitFetch", "Fetched ${outfitList.size} outfits")
                _outfits.value = outfitList
            } catch (e: Exception) {
                Log.e("OutfitFetch", "Error fetching outfits", e)
                _error.value = "Failed to load outfits: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load the user's favorite outfits from Firestore
    fun loadFavorites(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = firestore.collection("users")
                    .document(userId)
                    .collection("savedOutfits")
                    .get()
                    .await()

                val favorites = mutableMapOf<String, Boolean>()

                // Iterate over the saved outfits to populate favorite status
                result.documents.forEach { doc ->
                    val outfitId = doc.id
                    favorites[outfitId] = true  // If it exists, it's a favorite
                }

                // Update the favoriteMap in ViewModel
                _favoriteMap.value = favorites
            } catch (e: Exception) {
                _error.value = "Failed to load favorites: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Clear favorites when the user logs out or when no user is logged in
    fun clearFavorites() {
        _favoriteMap.value = emptyMap()
    }
    fun toggleFavorite(outfit: OutfitData) {
        val outfitId = outfit.id
        val isCurrentlyFavorite = _favoriteMap.value[outfitId] ?: false
        val newMap = _favoriteMap.value.toMutableMap()
        newMap[outfitId] = !isCurrentlyFavorite
        _favoriteMap.value = newMap

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore
        val outfitRef = db.collection("users")
            .document(userId)
            .collection("savedOutfits")
            .document(outfitId)

        viewModelScope.launch {
            try {
                if (!isCurrentlyFavorite) {
                    outfitRef.set(outfit)  // If it's now a favorite, save it to Firestore
                } else {
                    outfitRef.delete()  // If it's removed from favorites, delete from Firestore
                }
            } catch (e: Exception) {
                // Revert the favorite status if the Firestore operation fails
                val revertMap = _favoriteMap.value.toMutableMap()
                revertMap[outfitId] = isCurrentlyFavorite
                _favoriteMap.value = revertMap
                _error.value = "Failed to update favorite status: ${e.message}"
            }
        }
    }

    fun observeUser(userIdFlow: StateFlow<String?>) {
        viewModelScope.launch {
            userIdFlow.collect { userId ->
                if (userId == null) {
                    clearFavorites()
                } else {
                    loadFavorites(userId)
                }
            }
        }
    }
    fun fetchSavedOutfits() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore

        db.collection("users")
            .document(userId)
            .collection("savedOutfits")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val saved = snapshot.documents.mapNotNull { it.toObject(OutfitData::class.java) }
                _savedOutfits.value = saved

                // Update favorite map
                val newFavoriteMap = mutableMapOf<String, Boolean>()
                saved.forEach { newFavoriteMap[it.id] = true }
                _favoriteMap.value = newFavoriteMap
            }
    }
}