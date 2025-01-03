package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ProductListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val allProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        recyclerView = findViewById(R.id.recyclerViewProductList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetching products from Firestore
        fetchProductsFromFirestore()

        // Handle product click
        productAdapter = ProductAdapter(allProducts) { product ->
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra("product", product) // Pass the product to the details screen
            startActivity(intent)
        }
    }

    private fun fetchProductsFromFirestore() {
        FirebaseFirestore.getInstance().collection("products")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    allProducts.add(product)
                }
                recyclerView.adapter = productAdapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
