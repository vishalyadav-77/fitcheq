package com.vayo.fitcheq.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MaleHomeViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    //fetch star boy outfits
//    Firebase.firestore.collection("outfits")
//    .whereEqualTo("gender", "male")
//    .whereArrayContains("tags", "starboy")
//    .get()
}