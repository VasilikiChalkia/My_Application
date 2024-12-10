package com.example.myapplication

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val rating: Float,
    val category: String,
    val imageResId: String // Assuming this is a URL (String)
) : Serializable {
    // No-argument constructor for Firestore deserialization
    constructor() : this(0, "", "", 0.0, 0.0f, "", "")
}

