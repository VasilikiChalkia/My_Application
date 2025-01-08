package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class WishlistActivity : AppCompatActivity() {
    private val wishlistViewModel: WishlistViewModel by viewModels()
    private lateinit var wishlistAdapter: WishlistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("WishlistActivity", "onCreate started")
        setContentView(R.layout.activity_wishlist)
        Log.d("WishlistActivity", "Content view set")

        val recyclerViewWishlist = findViewById<RecyclerView>(R.id.recyclerViewWishlist)
        val textTotalPrice = findViewById<TextView>(R.id.textViewTotalPrice)

        Log.d("WishlistActivity", "Views initialized")

        // Setup RecyclerView with CartAdapter
        wishlistAdapter = WishlistAdapter(
            onQuantityChanged = { wishlistItem, newQuantity ->
                Log.d("WishlistActivity", "Quantity changed for ${wishlistItem.product.name} to $newQuantity")
                wishlistViewModel.updateItemQuantity(wishlistItem, newQuantity)
            },
            onItemRemoved = { wishlistItem ->
                Log.d("CartActivity", "Removing item ${wishlistItem.product.name} from cart")
                wishlistViewModel.removeFromWishList(wishlistItem.product)
            }
        )
        recyclerViewWishlist.layoutManager = LinearLayoutManager(this)
        recyclerViewWishlist.adapter = wishlistAdapter
        Log.d("CartActivity", "RecyclerView setup completed with CartAdapter")

        // Observe cart items and update RecyclerView
        wishlistViewModel.wishlist.observe(this) { wishlist ->
            Log.d("CartActivity", "Cart items updated. Total items: ${wishlist.size}")
            wishlistAdapter.submitList(wishlist)

            val totalPrice = wishlistViewModel.calculateTotal()
            textTotalPrice.text = if (wishlist.isEmpty()) {
                Log.d("CartActivity", "Cart is empty")
                "Your cart is empty."
            } else {
                Log.d("CartActivity", "Total price updated: $totalPrice")
                "Total: $${String.format("%.2f", totalPrice)}"
            }
        }



        // Load cart data from Firestore
        Log.d("CartActivity", "Loading cart data from Firestore")
        wishlistViewModel.loadWishListFromFirestore()
    }
}
