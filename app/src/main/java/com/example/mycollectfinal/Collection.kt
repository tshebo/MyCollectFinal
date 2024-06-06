package com.example.mycollectfinal

import android.icu.util.CurrencyAmount
import android.net.Uri
import com.google.firebase.Timestamp

data class Item(
    val name: String,
    val description: String,
    val dateOfAcquisition: String,
    val quantity: Int,
    val imageUri: String?
)

data class Collection(
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val imageUri: String? = null,
    val goalAmount: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", null, 0, Timestamp.now())
}

