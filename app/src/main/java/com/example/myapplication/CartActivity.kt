package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class CartActivity : AppCompatActivity() {
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val recyclerViewCart = findViewById<RecyclerView>(R.id.recyclerViewCart) // RecyclerView for cart items
        val textTotalPrice = findViewById<TextView>(R.id.textViewTotalPrice) // TextView for total price
        val buttonCheckout = findViewById<Button>(R.id.buttonCheckout) // Button for checkout

        // Setup RecyclerView with CartAdapter
        cartAdapter = CartAdapter(
            onQuantityChanged = { cartItem, newQuantity ->
                cartViewModel.updateItemQuantity(cartItem, newQuantity)
            },
            onItemRemoved = { cartItem ->
                cartViewModel.removeFromCart(cartItem.product)
            }
        )
        recyclerViewCart.layoutManager = LinearLayoutManager(this)
        recyclerViewCart.adapter = cartAdapter

        // Observe cart items and update RecyclerView
        cartViewModel.cartItems.observe(this) { cartItems ->
            cartAdapter.submitList(cartItems)

            val totalPrice = cartViewModel.calculateTotal()
            textTotalPrice.text = if (cartItems.isEmpty()) {
                "Your cart is empty."
            } else {
                "Total: $${String.format("%.2f", totalPrice)}"
            }
        }

        buttonCheckout.setOnClickListener {
            // Handle checkout logic here
            Toast.makeText(this, "Proceeding to checkout!", Toast.LENGTH_SHORT).show()
        }

        // Load cart data from Firestore
        cartViewModel.loadCartFromFirestore()
    }
}

