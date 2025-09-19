package com.vayo.fitcheq.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.vayo.fitcheq.data.model.AppliedFilters
import com.vayo.fitcheq.data.model.AvailableFilters
import com.vayo.fitcheq.data.model.Filters
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

    private val _relatedOutfits = MutableStateFlow<List<OutfitData>>(emptyList())
    val relatedOutfits: StateFlow<List<OutfitData>> = _relatedOutfits

    private var lastVisibleDoc: DocumentSnapshot? = null
    private var isLastPageReached = false
    private var isFetching = false

    private val _availableFilters = MutableStateFlow(AvailableFilters())
    val availableFilters: StateFlow<AvailableFilters> = _availableFilters

    private val _appliedFilters = MutableStateFlow(AppliedFilters())
    val appliedFilters: StateFlow<AppliedFilters> = _appliedFilters



    fun fetchOutfitsByFieldAndGender(
        context: Context,
        fieldName: String,
        fieldValue: String,
        gender: String,
        reset: Boolean = false,
        pageSize: Long = 15L) {
        viewModelScope.launch {
            // prevent concurrent fetches
            if (isFetching) return@launch
            if (reset) clearOutfits()
            if (isLastPageReached) return@launch
            isFetching = true
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("OutfitFetch", "Fetching outfits where $fieldName=$fieldValue and gender=$gender")

                val baseQuery= Firebase.firestore.collection("outfits")
                    .whereIn("gender", listOf(gender, "unisex"))
                val arrayFields = setOf("tags", "style", "occasion","season")

                var finalQuery = if (fieldName in arrayFields) {
                    baseQuery.whereArrayContains(fieldName, fieldValue)
                } else {
                    baseQuery.whereEqualTo(fieldName, fieldValue)
                }

                finalQuery = finalQuery
                    .orderBy(FieldPath.documentId())
                    .limit(pageSize)

                lastVisibleDoc?.let { last ->
                    // start after last fetched doc id
                    finalQuery = finalQuery.startAfter(last.id)
                }

                val result = finalQuery.get().await()

                val outfitList = result.documents.mapNotNull { doc ->
                    doc.toObject(OutfitData::class.java)?.copy(id = doc.id)
                }

                // update vs replace depending on reset
                _outfits.value = if (reset) outfitList else _outfits.value + outfitList
                // ✅ prefetch those 15 images
                prefetchImages(context.applicationContext, outfitList)

                lastVisibleDoc = result.documents.lastOrNull()
                if (result.documents.size < pageSize.toInt()) {
                    isLastPageReached = true
                }

            } catch (e: Exception) {
                Log.e("OutfitFetch", "Error fetching outfits", e)
                _error.value = "Failed to load outfits: ${e.message}"
            } finally {
                _isLoading.value = false
                isFetching = false
            }
        }
    }

    // Clear outfits to prevent showing previous screen content
    fun clearOutfits() {
        _outfits.value = emptyList()
        lastVisibleDoc = null
        isLastPageReached = false
        _error.value = null
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

    fun fetchRelatedOutfits(currentOutfit: OutfitData) {
        viewModelScope.launch {
            // Assuming 'outfits' is already loaded in memory (from repository)
            val allOutfits = outfits.value // StateFlow/LiveData of all outfits
            val filtered = allOutfits.filter {
                it.category == currentOutfit.category && it.id != currentOutfit.id && it.type == currentOutfit.type
            }
            _relatedOutfits.value = filtered
        }
    }

    private fun prefetchImages(context: Context, outfits: List<OutfitData>) {
        val imageLoader = ImageLoader(context)
        outfits.forEach { outfit ->
            outfit.imageUrl?.let { url ->
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
                imageLoader.enqueue(request)
            }
        }
    }

    fun fetchAvailableFilters(gender: String, fieldName: String, fieldValue: String) {
        viewModelScope.launch {
            try {
                val gendersToFetch = listOf(gender, "unisex") // both gender + unisex
                val mergedFilters = AvailableFilters(
                    categories = mutableSetOf(),
                    brands = mutableSetOf(),
                    colors = mutableSetOf(),
                    fits = mutableSetOf(),
                    types = mutableSetOf()
                )

                gendersToFetch.forEach { g ->
                    val docId = "${fieldName}_${fieldValue}_$g"
                    val doc = firestore.collection("filters")
                        .document(docId)
                        .get()
                        .await()

                    if (doc.exists()) {
                        val data = doc.data ?: return@forEach

                        mergedFilters.categories += (data["categories"] as? List<String>)?.toSet() ?: emptySet()
                        mergedFilters.brands += (data["brand"] as? List<String>)?.toSet() ?: emptySet()
                        mergedFilters.colors += (data["colors"] as? List<String>)?.toSet() ?: emptySet()
                        mergedFilters.fits += (data["fits"] as? List<String>)?.toSet() ?: emptySet()
                        mergedFilters.types += (data["type"] as? List<String>)?.toSet() ?: emptySet()
                    }
                }

                // Update StateFlow once with merged results
                _availableFilters.value = mergedFilters

            } catch (e: Exception) {
                Log.e("FilterFetch", "Error fetching filters", e)
                _availableFilters.value = AvailableFilters() // fallback
            }
        }
    }


    fun fetchFilteredOutfits(
        context: Context,
        fieldName: String,
        fieldValue: String,
        gender: String,
        filters: Filters,
        reset: Boolean = true,
        pageSize: Long = 15L
    ) {
        viewModelScope.launch {
            // Reset state if new search
            if (reset) {
                clearOutfits()
                lastVisibleDoc = null
                isLastPageReached = false
            }
            if (isFetching || isLastPageReached) return@launch

            isFetching = true
            _isLoading.value = true
            _error.value = null

            try {
                // Base query: match current screen field + gender/unisex
                val baseQuery: Query = Firebase.firestore.collection("outfits")
                    .whereIn("gender", listOf(gender, "unisex"))

                val arrayFields = setOf("tags", "style", "occasion", "season")
                var finalQuery: Query = if (fieldName in arrayFields) {
                    baseQuery.whereArrayContains(fieldName, fieldValue)
                } else {
                    baseQuery.whereEqualTo(fieldName, fieldValue)
                }

                // Apply filters
                filters.categories.takeIf { it.isNotEmpty() }?.let {
                    finalQuery = finalQuery.whereIn("category", it.toList())
                }
                filters.websites.takeIf { it.isNotEmpty() }?.let {
                    finalQuery = finalQuery.whereIn("website", it.toList())
                }
                filters.colors.takeIf { it.isNotEmpty() }?.let {
                    finalQuery = finalQuery.whereIn("color", it.toList())
                }
                filters.type?.let { finalQuery = finalQuery.whereEqualTo("type", it) }
                filters.fits.takeIf { it.isNotEmpty() }?.let {
                    finalQuery = finalQuery.whereIn("fit", it.toList())
                }

                // Pagination
                lastVisibleDoc?.let { last ->
                    finalQuery = finalQuery.startAfter(last)
                }

                finalQuery = finalQuery.orderBy(FieldPath.documentId()).limit(pageSize)

                // Fetch data
                val result = finalQuery.get().await()
                val outfitList = result.documents.mapNotNull { doc ->
                    doc.toObject(OutfitData::class.java)?.copy(id = doc.id)
                }

                // Append or replace based on reset
                _outfits.value = if (reset) outfitList else _outfits.value + outfitList

                // Prefetch images for smoother UI
                prefetchImages(context.applicationContext, outfitList)

                lastVisibleDoc = result.documents.lastOrNull()
                if (result.documents.size < pageSize.toInt()) {
                    isLastPageReached = true
                }

            } catch (e: Exception) {
                Log.e("OutfitFetch", "Error fetching filtered outfits", e)
                _error.value = "Failed to fetch outfits: ${e.message}"
            } finally {
                _isLoading.value = false
                isFetching = false
            }
        }
    }




}