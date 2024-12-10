package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCart)
        val textTotal = findViewById<TextView>(R.id.textViewTotalPrice)

        recyclerView.layoutManager = LinearLayoutManager(this)

        cartViewModel.cartItems.observe(this) { cartItems ->
            recyclerView.adapter = CartAdapter(cartItems) { cartItem ->
                cartViewModel.removeFromCart(cartItem.product)
            }
            textTotal.text = "Total: $${cartViewModel.calculateTotal()}"
        }
    }
}
