package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    fun addToCart(product: Product) {
        val existingItem = _cartItems.value?.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            _cartItems.value?.add(CartItem(product, 1))
        }
        _cartItems.value = _cartItems.value // Trigger UI update
    }

    fun removeFromCart(product: Product) {
        _cartItems.value?.removeIf { it.product.id == product.id }
        _cartItems.value = _cartItems.value
    }

    fun updateQuantity(product: Product, newQuantity: Int) {
        val cartItem = _cartItems.value?.find { it.product.id == product.id }
        if (cartItem != null) {
            if (newQuantity <= 0) {
                removeFromCart(product)
            } else {
                cartItem.quantity = newQuantity
            }
        }
        _cartItems.value = _cartItems.value
    }

    fun calculateTotal(): Double {
        return _cartItems.value?.sumOf { it.product.price * it.quantity } ?: 0.0
    }
}
