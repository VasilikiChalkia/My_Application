package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ProductDetailsActivity : AppCompatActivity() {

    private val cartViewModel: CartViewModel by viewModels()
    private val wishlistViewModel: WishlistViewModel by viewModels()

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
            val buttonAddToWishlist = findViewById<Button>(R.id.buttonAddToWishlist) // New Button

            // Load image using Glide
            Glide.with(this)
                .load(product.imageResId) // Ensure `imageResId` is being passed correctly
                .into(imageView)

            // Set product details
            textName.text = product.name
            textDescription.text = product.description
            textPrice.text = "$${product.price}"

            // Handle "Add to Cart" action
            buttonAddToCart.setOnClickListener {
                // Add the product to the cart
                cartViewModel.addToCart(product)

                // Show a toast message
                Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()

                // Navigate to CartActivity
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }

            // Handle "Add to Wishlist" action
            buttonAddToWishlist.setOnClickListener {
                // Create a WishlistItem object
                wishlistViewModel.addToWishlist(product)

                Toast.makeText(this, "${product.name} added to wish list!", Toast.LENGTH_SHORT).show()

                // Navigate to CartActivity
                val intent = Intent(this, WishlistActivity::class.java)
                startActivity(intent)
            }


        }
    }
}

