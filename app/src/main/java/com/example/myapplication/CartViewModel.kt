package com.example.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    private val db = FirebaseFirestore.getInstance()

    // Add a product to the cart
    fun addToCart(product: Product) {
        val existingItem = _cartItems.value?.find { it.product.id == product?.id   }
        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            _cartItems.value?.add(CartItem(product, 1))
        }
        _cartItems.value = _cartItems.value // Trigger LiveData update
        saveCartToFirestore()
    }

    // Update item quantity
    fun updateItemQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity > 0) {
            _cartItems.value = _cartItems.value?.map {
                if (it.product.id == cartItem.product.id) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }?.toMutableList()
        } else {
            removeFromCart(cartItem.product)
        }
        saveCartToFirestore()
    }

    // Save the cart to Firestore
    private fun saveCartToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartCollectionRef = db.collection("users").document(userId).collection("cart")

        _cartItems.value?.forEach { cartItem ->
            val productRef = cartCollectionRef.document(cartItem.product.id.toString())
            productRef.set(
                mapOf(
                    "productId" to cartItem.product.id,
                    "quantity" to cartItem.quantity,
                    "name" to cartItem.product.name,
                    "price" to cartItem.product.price
                ),
                SetOptions.merge()
            ).addOnSuccessListener {
                Log.d("CartViewModel", "Cart item ${cartItem.product.name} successfully saved to Firestore")
            }.addOnFailureListener { e ->
                Log.e("CartViewModel", "Error saving cart item ${cartItem.product.name} to Firestore", e)
            }
        }
    }

    // Remove a product from the cart
    fun removeFromCart(product: Product) {
        _cartItems.value?.removeIf { it.product.id == product.id }
        _cartItems.value = _cartItems.value // Trigger LiveData update
        saveCartToFirestore()
    }

    // Calculate total price
    fun calculateTotal(): Double {
        return _cartItems.value?.sumOf { it.product.price * it.quantity } ?: 0.0
    }

    // Load cart from Firestore
    fun loadCartFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartCollectionRef = db.collection("users").document(userId).collection("cart")

        cartCollectionRef.get().addOnSuccessListener { documents ->
            val cartItems = mutableListOf<CartItem>()
            for (document in documents) {
                val productId = document.getLong("productId")?.toInt() ?: continue
                val quantity = document.getLong("quantity")?.toInt() ?: continue
                val name = document.getString("name") ?: ""
                val price = document.getDouble("price") ?: 0.0

                val product = Product(
                    id = productId,
                    name = name,
                    description = "Product Description",
                    price = price,
                    rating = 0.0,
                    category = "Category",
                    imageResId = "ImageResId",
                    totalReviews = 0
                )

                cartItems.add(CartItem(product, quantity))
            }
            _cartItems.value = cartItems
        }.addOnFailureListener { e ->
            Log.e("CartViewModel", "Error loading cart from Firestore", e)
        }
    }
}