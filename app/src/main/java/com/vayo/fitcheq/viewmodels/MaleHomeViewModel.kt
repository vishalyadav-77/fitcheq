package com.vayo.fitcheq.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.vayo.fitcheq.data.model.OutfitData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MaleHomeViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

//    fetch star boy outfits
//    Firebase.firestore.collection("outfits")
//    .whereEqualTo("gender", "male")
//    .whereArrayContains("tags", "starboy")
//    .get()
    private val _outfits = MutableStateFlow<List<OutfitData>>(emptyList())
    val outfits: StateFlow<List<OutfitData>> = _outfits
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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
}