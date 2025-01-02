package com.example.myapplication

import android.os.Bundle
import android.util.Log
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
        Log.d("CartActivity", "onCreate started")
        setContentView(R.layout.activity_cart)
        Log.d("CartActivity", "Content view set")

        val recyclerViewCart = findViewById<RecyclerView>(R.id.recyclerViewCart)
        val textTotalPrice = findViewById<TextView>(R.id.textViewTotalPrice)
        val buttonCheckout = findViewById<Button>(R.id.buttonCheckout)
        Log.d("CartActivity", "Views initialized")

        // Setup RecyclerView with CartAdapter
        cartAdapter = CartAdapter(
            onQuantityChanged = { cartItem, newQuantity ->
                Log.d("CartActivity", "Quantity changed for ${cartItem.product.name} to $newQuantity")
                cartViewModel.updateItemQuantity(cartItem, newQuantity)
            },
            onItemRemoved = { cartItem ->
                Log.d("CartActivity", "Removing item ${cartItem.product.name} from cart")
                cartViewModel.removeFromCart(cartItem.product)
            }
        )
        recyclerViewCart.layoutManager = LinearLayoutManager(this)
        recyclerViewCart.adapter = cartAdapter
        Log.d("CartActivity", "RecyclerView setup completed with CartAdapter")

        // Observe cart items and update RecyclerView
        cartViewModel.cartItems.observe(this) { cartItems ->
            Log.d("CartActivity", "Cart items updated. Total items: ${cartItems.size}")
            cartAdapter.submitList(cartItems)

            val totalPrice = cartViewModel.calculateTotal()
            textTotalPrice.text = if (cartItems.isEmpty()) {
                Log.d("CartActivity", "Cart is empty")
                "Your cart is empty."
            } else {
                Log.d("CartActivity", "Total price updated: $totalPrice")
                "Total: $${String.format("%.2f", totalPrice)}"
            }
        }

        buttonCheckout.setOnClickListener {
            Log.d("CartActivity", "Checkout button clicked")
            Toast.makeText(this, "Proceeding to checkout!", Toast.LENGTH_SHORT).show()
            // Additional checkout logic can be added here
        }

        // Load cart data from Firestore
        Log.d("CartActivity", "Loading cart data from Firestore")
        cartViewModel.loadCartFromFirestore()
    }
}
