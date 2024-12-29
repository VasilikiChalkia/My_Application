package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ProductDetailsActivity : AppCompatActivity() {

    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val product = intent.getSerializableExtra("product") as? Product

        if (product != null) {
            val imageView = findViewById<ImageView>(R.id.imageViewProductDetails)
            val textName = findViewById<TextView>(R.id.textViewProductNameDetails)
            val textDescription = findViewById<TextView>(R.id.textViewProductDescription)
            val textPrice = findViewById<TextView>(R.id.textViewProductPriceDetails)
            val buttonAddToCart = findViewById<Button>(R.id.buttonAddToCart)

            // Load image using Glide (assuming imageResId is a URL)
            Glide.with(this)
                .load(product.imageResId)
                .into(imageView)

            // Set the product details on the UI
            textName.text = product.name
            textDescription.text = product.description
            textPrice.text = "$${product.price}"

            // Set click listener for "Add to Cart" button
            buttonAddToCart.setOnClickListener {
                // Add the product to the cart
                cartViewModel.addToCart(product)

                // Show a toast and log the action
                Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                Log.d("ProductDetailsActivity", "Product added to cart: ${product.name}")

                // Navigate to the Cart Activity
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }
        }
    }
}