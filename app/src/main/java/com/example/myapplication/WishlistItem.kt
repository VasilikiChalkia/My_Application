package com.example.myapplication

import java.io.Serializable

data class WishlistItem(
    val product: Product,
    var quantity: Int
) : Serializable