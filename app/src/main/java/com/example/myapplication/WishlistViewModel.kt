package com.example.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class WishlistViewModel : ViewModel() {

    // Backing property for wishlist
    private val _wishlist = MutableLiveData<MutableList<WishlistItem>>(mutableListOf())
    val wishlist: LiveData<MutableList<WishlistItem>> = _wishlist

    private val db = FirebaseFirestore.getInstance()

    // Add an item to the wishlist
    fun addToWishlist(product: Product) {
        val existingItem = _wishlist.value?.find { it.product.id == product?.id   }
        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            _wishlist.value?.add(WishlistItem(product, 1))
        }
        _wishlist.value = _wishlist.value // Trigger LiveData update
        saveWishlistToFirestore()
    }

    fun calculateTotal(): Double {
        return _wishlist.value?.sumOf { it.product.price * it.quantity } ?: 0.0
    }
    // Remove an item from the wishlist
    fun updateItemQuantity(wishlistItem: WishlistItem, newQuantity: Int) {
        if (newQuantity > 0) {
            _wishlist.value = _wishlist.value?.map {
                if (it.product.id == wishlistItem.product.id) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }?.toMutableList()
        } else {
            removeFromWishList(wishlistItem.product)
        }
        saveWishlistToFirestore()
    }

    // Save the wishlist to Firestore
    private fun saveWishlistToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val wishlistCollectionRef = db.collection("users").document(userId).collection("wishlist")

        // Using background thread for Firestore update (e.g., using async tasks, or coroutines)
        Thread {
            _wishlist.value?.forEach { wishlistItem ->
                val productRef = wishlistCollectionRef.document(wishlistItem.product.id.toString())
                productRef.set(
                    mapOf(
                        "productId" to wishlistItem.product.id,
                        "quantity" to wishlistItem.quantity,
                        "name" to wishlistItem.product.name,
                        "price" to wishlistItem.product.price
                    ),
                    SetOptions.merge()
                ).addOnSuccessListener {
                    Log.d("WishlistViewModel", "Wishlist item ${wishlistItem.product.name} successfully saved to Firestore")
                }.addOnFailureListener { e ->
                    Log.e("WishlistViewModel", "Error saving wishlist item ${wishlistItem.product.name} to Firestore", e)
                }
            }
        }.start()
    }

    // Remove a product from the wishlist
    fun removeFromWishList(product: Product) {
        _wishlist.value?.removeIf { it.product.id == product.id }
        _wishlist.value = _wishlist.value // Trigger LiveData update
        saveWishlistToFirestore()
    }

    // Load wishlist from Firestore
    fun loadWishListFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val wishlistCollectionRef = db.collection("users").document(userId).collection("wishlist")

        wishlistCollectionRef.get().addOnSuccessListener { documents ->
            val wishlistItems = mutableListOf<WishlistItem>()
            for (document in documents) {
                val productId = document.getLong("productId")?.toInt() ?: continue
                val quantity = document.getLong("quantity")?.toInt() ?: continue
                val name = document.getString("name") ?: ""
                val price = document.getDouble("price") ?: 0.0

                val product = Product(
                    id = productId,
                    name = name,
                    description = "Product Description", // This can be adjusted as needed
                    price = price,
                    rating = 0.0, // Assuming a default value, adjust as needed
                    category = "Category", // Assuming a default value, adjust as needed
                    imageResId = "ImageResId", // Assuming a default value, adjust as needed
                    totalReviews = 0 // Assuming a default value, adjust as needed
                )

                wishlistItems.add(WishlistItem(product, quantity))
            }
            _wishlist.value = wishlistItems // Correct assignment to LiveData
        }.addOnFailureListener { e ->
            Log.e("WishlistViewModel", "Error loading wishlist from Firestore", e)
        }
    }
}