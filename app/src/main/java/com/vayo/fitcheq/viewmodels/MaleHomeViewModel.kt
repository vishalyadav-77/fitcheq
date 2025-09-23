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

    // Pagination state
    private var lastVisibleDoc: DocumentSnapshot? = null
    private var isLastPageReached = false
    private var isFetching = false

    // Filter state to track what's currently being used
    private var currentFilters: Filters? = null
    private var currentFieldName: String? = null
    private var currentFieldValue: String? = null
    private var currentGender: String? = null

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

        Log.d("MaleHomeViewModel", "🔄 fetchOutfitsByFieldAndGender called - fieldName: $fieldName, fieldValue: $fieldValue, gender: $gender, reset: $reset")

        viewModelScope.launch {
            // prevent concurrent fetches
            if (isFetching) {
                Log.d("MaleHomeViewModel", "⏸️ Already fetching, skipping...")
                return@launch
            }

            // Always reset when switching from filtered to unfiltered or when explicitly requested
            if (reset || currentFilters != null) {
                Log.d("MaleHomeViewModel", "🔄 Resetting state (switching from filtered to unfiltered or explicit reset)...")
                clearOutfits()
                currentFilters = null
            }

            if (isLastPageReached && !reset && currentFilters == null) {
                Log.d("MaleHomeViewModel", "🚫 Last page reached, skipping...")
                return@launch
            }

            // Store current fetch params
            currentFieldName = fieldName
            currentFieldValue = fieldValue
            currentGender = gender

            isFetching = true

            try {
                _isLoading.value = true
                _error.value = null

                Log.d("MaleHomeViewModel", "📡 Building query for $fieldName=$fieldValue and gender=$gender")

                val baseQuery = Firebase.firestore.collection("outfits")
                    .whereIn("gender", listOf(gender, "unisex"))

                val arrayFields = setOf("tags", "style", "occasion","season")

                var finalQuery = if (fieldName in arrayFields) {
                    Log.d("MaleHomeViewModel", "🎯 Using array contains for field: $fieldName")
                    baseQuery.whereArrayContains(fieldName, fieldValue)
                } else {
                    Log.d("MaleHomeViewModel", "🎯 Using equal to for field: $fieldName")
                    baseQuery.whereEqualTo(fieldName, fieldValue)
                }

                // Apply ordering first, then pagination
                finalQuery = finalQuery.orderBy(FieldPath.documentId())

                lastVisibleDoc?.let { last ->
                    Log.d("MaleHomeViewModel", "📄 Starting after document: ${last.id}")
                    finalQuery = finalQuery.startAfter(last.id)
                }

                finalQuery = finalQuery.limit(pageSize)

                val result = finalQuery.get().await()
                Log.d("MaleHomeViewModel", "✅ Query returned ${result.documents.size} documents")

                val outfitList = result.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(OutfitData::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("MaleHomeViewModel", "❌ Error parsing document ${doc.id}", e)
                        null
                    }
                }

                Log.d("MaleHomeViewModel", "📊 Successfully parsed ${outfitList.size} outfits")

                // update vs replace depending on reset
                val shouldReset = reset || currentFilters != null
                _outfits.value = if (shouldReset) {
                    Log.d("MaleHomeViewModel", "🔄 Replacing outfits with new list")
                    outfitList
                } else {
                    Log.d("MaleHomeViewModel", "➕ Appending ${outfitList.size} outfits to existing ${_outfits.value.size}")
                    _outfits.value + outfitList
                }

                // prefetch images
                prefetchImages(context.applicationContext, outfitList)

                lastVisibleDoc = result.documents.lastOrNull()
                if (result.documents.size < pageSize.toInt()) {
                    Log.d("MaleHomeViewModel", "🏁 Last page reached (${result.documents.size} < $pageSize)")
                    isLastPageReached = true
                }

                Log.d("MaleHomeViewModel", "✅ Fetch completed. Total outfits: ${_outfits.value.size}")

            } catch (e: Exception) {
                Log.e("MaleHomeViewModel", "❌ Error fetching outfits", e)
                _error.value = "Failed to load outfits: ${e.message}"
            } finally {
                _isLoading.value = false
                isFetching = false
            }
        }
    }

    fun clearOutfits() {
        Log.d("MaleHomeViewModel", "🧹 Clearing outfits and pagination state")
        _outfits.value = emptyList()
        lastVisibleDoc = null
        isLastPageReached = false
        _error.value = null
        currentFilters = null
    }

    fun loadFavorites(userId: String) {
        Log.d("MaleHomeViewModel", "❤️ Loading favorites for user: $userId")
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

                result.documents.forEach { doc ->
                    val outfitId = doc.id
                    favorites[outfitId] = true
                }

                Log.d("MaleHomeViewModel", "❤️ Loaded ${favorites.size} favorites")
                _favoriteMap.value = favorites

            } catch (e: Exception) {
                Log.e("MaleHomeViewModel", "❌ Error loading favorites", e)
                _error.value = "Failed to load favorites: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearFavorites() {
        Log.d("MaleHomeViewModel", "💔 Clearing favorites")
        _favoriteMap.value = emptyMap()
    }

    fun toggleFavorite(outfit: OutfitData) {
        val outfitId = outfit.id
        val isCurrentlyFavorite = _favoriteMap.value[outfitId] ?: false
        val newMap = _favoriteMap.value.toMutableMap()
        newMap[outfitId] = !isCurrentlyFavorite
        _favoriteMap.value = newMap

        Log.d("MaleHomeViewModel", "❤️ Toggling favorite for ${outfit.id}: ${!isCurrentlyFavorite}")

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore
        val outfitRef = db.collection("users")
            .document(userId)
            .collection("savedOutfits")
            .document(outfitId)

        viewModelScope.launch {
            try {
                if (!isCurrentlyFavorite) {
                    Log.d("MaleHomeViewModel", "❤️ Adding to favorites: ${outfit.id}")
                    outfitRef.set(outfit)
                } else {
                    Log.d("MaleHomeViewModel", "💔 Removing from favorites: ${outfit.id}")
                    outfitRef.delete()
                }
            } catch (e: Exception) {
                Log.e("MaleHomeViewModel", "❌ Error updating favorite", e)
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

        Log.d("MaleHomeViewModel", "💾 Setting up saved outfits listener for user: $userId")

        db.collection("users")
            .document(userId)
            .collection("savedOutfits")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.e("MaleHomeViewModel", "❌ Error in saved outfits listener", error)
                    return@addSnapshotListener
                }

                val saved = snapshot.documents.mapNotNull { it.toObject(OutfitData::class.java) }
                _savedOutfits.value = saved

                val newFavoriteMap = mutableMapOf<String, Boolean>()
                saved.forEach { newFavoriteMap[it.id] = true }
                _favoriteMap.value = newFavoriteMap

                Log.d("MaleHomeViewModel", "💾 Updated saved outfits: ${saved.size} items")
            }
    }

    fun fetchRelatedOutfits(currentOutfit: OutfitData) {
        Log.d("MaleHomeViewModel", "🔗 Fetching related outfits for: ${currentOutfit.id}")
        viewModelScope.launch {
            val allOutfits = outfits.value
            val filtered = allOutfits.filter {
                it.category == currentOutfit.category &&
                        it.id != currentOutfit.id &&
                        it.type == currentOutfit.type
            }
            Log.d("MaleHomeViewModel", "🔗 Found ${filtered.size} related outfits")
            _relatedOutfits.value = filtered
        }
    }

    private fun prefetchImages(context: Context, outfits: List<OutfitData>) {
        Log.d("MaleHomeViewModel", "🖼️ Prefetching ${outfits.size} images")
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
        Log.d("MaleHomeViewModel", "🎛️ Fetching available filters for: gender=$gender, field=$fieldName, value=$fieldValue")
        viewModelScope.launch {
            try {
                val gendersToFetch = listOf(gender, "unisex")
                val mergedFilters = AvailableFilters(
                    categories = mutableSetOf(),
                    brands = mutableSetOf(),
                    colors = mutableSetOf(),
                    fits = mutableSetOf(),
                    types = mutableSetOf()
                )

                gendersToFetch.forEach { g ->
                    val docId = "${fieldName}_${fieldValue}_$g"
                    Log.d("MaleHomeViewModel", "🎛️ Fetching filter document: $docId")

                    val doc = firestore.collection("filters")
                        .document(docId)
                        .get()
                        .await()

                    if (doc.exists()) {
                        val data = doc.data ?: return@forEach
                        Log.d("MaleHomeViewModel", "✅ Filter document exists for $docId")

                        mergedFilters.categories += (data["categories"] as? List<String>)?.toSet() ?: emptySet()
                        mergedFilters.brands += (data["brand"] as? List<String>)?.toSet() ?: emptySet()
                        mergedFilters.colors += (data["colors"] as? List<String>)?.toSet() ?: emptySet()
                        mergedFilters.fits += (data["fits"] as? List<String>)?.toSet() ?: emptySet()
                        mergedFilters.types += (data["type"] as? List<String>)?.toSet() ?: emptySet()
                    } else {
                        Log.w("MaleHomeViewModel", "⚠️ Filter document does not exist: $docId")
                    }
                }

                Log.d("MaleHomeViewModel", "🎛️ Merged filters - Categories: ${mergedFilters.categories.size}, Brands: ${mergedFilters.brands.size}, Colors: ${mergedFilters.colors.size}, Types: ${mergedFilters.types.size}, Fits: ${mergedFilters.fits.size}")
                _availableFilters.value = mergedFilters

            } catch (e: Exception) {
                Log.e("MaleHomeViewModel", "❌ Error fetching filters", e)
                _availableFilters.value = AvailableFilters()
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
        Log.d("MaleHomeViewModel", "🎯 fetchFilteredOutfits called - fieldName: $fieldName, fieldValue: $fieldValue, gender: $gender, reset: $reset")
        Log.d("MaleHomeViewModel", "🎯 Filters: $filters")

        viewModelScope.launch {
            // Reset pagination state if new search or filters changed
            if (reset || currentFilters != filters) {
                Log.d("MaleHomeViewModel", "🔄 Resetting for new filter search")
                clearOutfits()
                lastVisibleDoc = null
                isLastPageReached = false
                currentFilters = filters
                currentFieldName = fieldName
                currentFieldValue = fieldValue
                currentGender = gender
            }

            if (isFetching || isLastPageReached) {
                Log.d("MaleHomeViewModel", if (isFetching) "⏸️ Already fetching" else "🚫 Last page reached")
                return@launch
            }

            isFetching = true
            _isLoading.value = true
            _error.value = null

            try {
                // Base query: match current screen field + gender/unisex
                Log.d("MaleHomeViewModel", "🔨 Building base query...")
                val baseQuery: Query = Firebase.firestore.collection("outfits")
                    .whereIn("gender", listOf(gender, "unisex"))

                val arrayFields = setOf("tags", "style", "occasion", "season")
                var finalQuery: Query = if (fieldName in arrayFields) {
                    Log.d("MaleHomeViewModel", "🎯 Using array contains for field: $fieldName")
                    baseQuery.whereArrayContains(fieldName, fieldValue)
                } else {
                    Log.d("MaleHomeViewModel", "🎯 Using equal to for field: $fieldName")
                    baseQuery.whereEqualTo(fieldName, fieldValue)
                }

                // Apply filters with detailed logging
                filters.categories.takeIf { it.isNotEmpty() }?.let { categories ->
                    Log.d("MaleHomeViewModel", "🎯 Applying category filter: $categories")
                    finalQuery = finalQuery.whereIn("category", categories.toList())
                }

                filters.websites.takeIf { it.isNotEmpty() }?.let { websites ->
                    Log.d("MaleHomeViewModel", "🎯 Applying website filter: $websites")
                    finalQuery = finalQuery.whereIn("website", websites.toList())
                }

                filters.colors.takeIf { it.isNotEmpty() }?.let { colors ->
                    Log.d("MaleHomeViewModel", "🎯 Applying color filter: $colors")
                    finalQuery = finalQuery.whereIn("color", colors.toList())
                }

                filters.type?.let { type ->
                    Log.d("MaleHomeViewModel", "🎯 Applying type filter: $type")
                    finalQuery = finalQuery.whereEqualTo("type", type)
                }

                filters.fits.takeIf { it.isNotEmpty() }?.let { fits ->
                    Log.d("MaleHomeViewModel", "🎯 Applying fit filter: $fits")
                    finalQuery = finalQuery.whereIn("fit", fits.toList())
                }

                // Price filtering will be done in-memory since Firestore can't handle string comparison properly

                // Apply ordering FIRST, then pagination
                finalQuery = finalQuery.orderBy(FieldPath.documentId())

                // Pagination
                lastVisibleDoc?.let { last ->
                    Log.d("MaleHomeViewModel", "📄 Starting after document: ${last.id}")
                    finalQuery = finalQuery.startAfter(last)
                }

                finalQuery = finalQuery.limit(pageSize)

                // Fetch data
                Log.d("MaleHomeViewModel", "📡 Executing filtered query...")
                val result = finalQuery.get().await()
                Log.d("MaleHomeViewModel", "✅ Filtered query returned ${result.documents.size} documents")

                val outfitList = result.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(OutfitData::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("MaleHomeViewModel", "❌ Error parsing document ${doc.id}", e)
                        null
                    }
                }

                // Apply price filter in-memory if needed
                val filteredOutfits = if (filters.priceRange != 0f..20000f) {
                    Log.d("MaleHomeViewModel", "💰 Applying price filter in-memory: ${filters.priceRange}")
                    outfitList.filter { outfit ->
                        outfit.price.toFloatOrNull()?.let { price ->
                            price in filters.priceRange
                        } ?: true
                    }
                } else {
                    outfitList
                }

                Log.d("MaleHomeViewModel", "📊 Final filtered list: ${filteredOutfits.size} outfits")

                // Append or replace based on reset
                _outfits.value = if (reset) {
                    Log.d("MaleHomeViewModel", "🔄 Replacing with filtered results")
                    filteredOutfits
                } else {
                    Log.d("MaleHomeViewModel", "➕ Appending filtered results")
                    _outfits.value + filteredOutfits
                }

                // Prefetch images
                prefetchImages(context.applicationContext, filteredOutfits)

                lastVisibleDoc = result.documents.lastOrNull()
                if (result.documents.size < pageSize.toInt()) {
                    Log.d("MaleHomeViewModel", "🏁 Last page reached for filtered results")
                    isLastPageReached = true
                }

                Log.d("MaleHomeViewModel", "✅ Filtered fetch completed. Total outfits: ${_outfits.value.size}")

            } catch (e: Exception) {
                Log.e("MaleHomeViewModel", "❌ Error fetching filtered outfits", e)
                _error.value = "Failed to fetch filtered outfits: ${e.message}"
            } finally {
                _isLoading.value = false
                isFetching = false
            }
        }
    }
}