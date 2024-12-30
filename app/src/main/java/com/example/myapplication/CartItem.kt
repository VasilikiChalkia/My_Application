package com.example.myapplication

import java.io.Serializable

data class CartItem(
    val product: Product,
    var quantity: Int
) : Serializable