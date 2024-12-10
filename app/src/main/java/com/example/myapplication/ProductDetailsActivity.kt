package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ProductDetailsActivity : AppCompatActivity() {

    // Reference to CartViewModel
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        // Retrieve the product object passed from the previous activity
        val product = intent.getSerializableExtra("product") as? Product

        if (product != null) {
            val imageView = findViewById<ImageView>(R.id.imageViewProductDetails)
            val textName = findViewById<TextView>(R.id.textViewProductNameDetails)
            val textDescription = findViewById<TextView>(R.id.textViewProductDescription)
            val textPrice = findViewById<TextView>(R.id.textViewProductPriceDetails)
            val buttonAddToCart = findViewById<Button>(R.id.buttonAddToCart)

            // Use Glide to load the image from the URL
            Glide.with(this)
                .load(product.imageResId) // Load the image from the URL
                .into(imageView)

            // Set the product details in the UI
            textName.text = product.name
            textDescription.text = product.description
            textPrice.text = "$${product.price}"

            // Set the action for the "Add to Cart" button
            buttonAddToCart.setOnClickListener {
                // Add the product to the cart using CartViewModel
                cartViewModel.addToCart(product)
            }
        }
    }
}
