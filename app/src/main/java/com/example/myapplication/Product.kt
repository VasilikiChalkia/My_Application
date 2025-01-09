package com.example.myapplication

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val category: String,
    val imageResId: String ,// Assuming this is a URL (String)
    val totalReviews: Int
) : Serializable {
    // No-argument constructor for Firestore deserialization
    constructor() : this(0, "", "", 0.0, 0.0, "", "", 0)
}
data class ProductSection(
    val category: String,
    val products: List<Product>
)
sealed class SectionItem {
    data class CategoryHeader(val categoryName: String) : SectionItem()
    data class ProductItem(val product: Product) : SectionItem()
}

data class Section(
    val category: String,
    val products: List<Product>
)

data class Review(
    val userId: String,
    val productId: Int,
    val rating: Float,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)
