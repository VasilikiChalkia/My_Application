package com.example.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CartViewModel : ViewModel() {
    // LiveData to hold the list of cart items
    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    // Firebase Firestore instance
    private val db = FirebaseFirestore.getInstance()

    // Add a product to the cart
    fun addToCart(product: Product) {
        val existingItem = _cartItems.value?.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity += 1 // Increase quantity if already in cart
        } else {
            _cartItems.value?.add(CartItem(product, 1)) // Add new product to the cart
        }
        // Trigger LiveData update by assigning a new value
        _cartItems.value = _cartItems.value // This is now correct to trigger LiveData update
        saveCartToFirestore()
    }

    // Save the cart to Firestore
    private fun saveCartToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Ensure the user is authenticated
        val cartCollectionRef = db.collection("users").document(userId).collection("cart")

        _cartItems.value?.forEach { cartItem ->
            val productRef = cartCollectionRef.document(cartItem.product.id.toString()) // Use product ID as the document ID

            // Save or update the cart item
            productRef.set(
                mapOf(
                    "productId" to cartItem.product.id,
                    "quantity" to cartItem.quantity,
                    "name" to cartItem.product.name,
                    "price" to cartItem.product.price
                ),
                SetOptions.merge() // Merge ensures only the specific fields are updated
            )
                .addOnSuccessListener {
                    Log.d("CartViewModel", "Cart item ${cartItem.product.name} successfully saved to Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("CartViewModel", "Error saving cart item ${cartItem.product.name} to Firestore", e)
                }
        }
    }



    // Load the cart from Firestore
    fun loadCartFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartCollectionRef = db.collection("users").document(userId).collection("cart")

        cartCollectionRef.get()
            .addOnSuccessListener { documents ->
                val cartItems = mutableListOf<CartItem>()
                for (document in documents) {
                    val productId = document.getLong("productId")?.toInt() ?: continue
                    val quantity = document.getLong("quantity")?.toInt() ?: continue
                    val name = document.getString("name") ?: ""
                    val price = document.getDouble("price") ?: 0.0

                    // Create a product object
                    val product = Product(
                        id = productId,
                        name = name,
                        description = "Product Description", // Placeholder
                        price = price,
                        rating = 0.0f, // Placeholder
                        category = "Category", // Placeholder
                        imageResId = "ImageResId" // Placeholder
                    )

                    cartItems.add(CartItem(product, quantity))
                }
                _cartItems.value = cartItems // Update LiveData
            }
            .addOnFailureListener { e ->
                Log.e("CartViewModel", "Error loading cart from Firestore", e)
            }
    }


    // Fetch product by id from Firestore or local database
    private fun fetchProductById(productId: Int): Product {
        // In a real application, fetch the product details from Firestore or a local database
        // For demonstration purposes, I'm returning a static product
        return Product(id = productId, name = "Product Name", description = "Product Description", price = 0.0, rating = 5.0f, category = "Category", imageResId = "ImageResId")
    }

    // Calculate the total price of all items in the cart
    fun calculateTotal(): Double {
        return _cartItems.value?.sumOf { it.product.price * it.quantity } ?: 0.0
    }

    // Remove a product from the cart
    fun removeFromCart(product: Product) {
        _cartItems.value?.removeIf { it.product.id == product.id } // Remove by product ID
        // Trigger LiveData update after modification
        _cartItems.value = _cartItems.value // This is now correct to trigger LiveData update
        saveCartToFirestore() // Update Firestore
    }

    // Clear all items from the cart
    fun clearCart() {
        _cartItems.value?.clear()
        // Trigger LiveData update after modification
        _cartItems.value = _cartItems.value // This is now correct to trigger LiveData update
        saveCartToFirestore() // Update Firestore
    }
}
