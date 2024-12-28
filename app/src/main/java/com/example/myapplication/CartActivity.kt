package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Reference to the UI components
        val layoutCartItems = findViewById<LinearLayout>(R.id.layoutCartItems) // LinearLayout to hold cart items
        val textTotal = findViewById<TextView>(R.id.textViewTotalPrice)

        // Load cart data from Firestore when the activity is created
        cartViewModel.loadCartFromFirestore()

        // Observe cart items and update UI
        cartViewModel.cartItems.observe(this) { cartItems ->
            layoutCartItems.removeAllViews()  // Remove previous views

            // Display each cart item
            cartItems.forEach { cartItem ->
                val textView = TextView(this)
                textView.text = "${cartItem.product.name} - $${cartItem.product.price} x ${cartItem.quantity}"
                layoutCartItems.addView(textView)
            }

            // Display total price
            val totalPrice = cartViewModel.calculateTotal()
            textTotal.text = "Total: $${totalPrice}"
        }
    }
}
