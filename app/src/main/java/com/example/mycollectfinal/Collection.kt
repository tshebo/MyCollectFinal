package com.example.mycollectfinal

import android.icu.util.CurrencyAmount
import android.net.Uri
import com.google.firebase.Timestamp

data class Item(
    val name: String = "",
    val description: String = "",
    val condition: String = "",
    val date: Timestamp = Timestamp.now(),
    val quantity: Int = 0,
    val imageUri: String? = null,
    val itemId: String = ""
)

data class Collection(
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val imageUri: String? = null,
    val goalAmount: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
) {

    constructor() : this("", "", "", null, 0, Timestamp.now())
}

//add the items page to view and edit item details
//add the achievements

