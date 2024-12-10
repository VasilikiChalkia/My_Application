package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCatalogue)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch products from Firestore
        fetchProductsFromFirestore()
    }

    private fun fetchProductsFromFirestore() {
        val productsCollection = firestore.collection("products")

        productsCollection.get()
            .addOnSuccessListener { documents ->
                val productList = mutableListOf<Product>()

                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }

                // Set up the RecyclerView adapter
                productAdapter = ProductAdapter(productList) { product ->
                    val intent = Intent(this, ProductDetailsActivity::class.java)
                    intent.putExtra("product", product) // 'product' must implement Serializable
                    startActivity(intent)
                }
                recyclerView.adapter = productAdapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}