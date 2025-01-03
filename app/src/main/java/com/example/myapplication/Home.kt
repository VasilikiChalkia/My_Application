package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var sectionedAdapter: SectionedProductAdapter
    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val allProducts = mutableListOf<Product>()
    private var filteredProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerViewSections)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val spinnerCategories = findViewById<Spinner>(R.id.spinnerCategories)
        val spinnerRatings = findViewById<Spinner>(R.id.spinnerRatings)
        val editTextMinPrice = findViewById<EditText>(R.id.editTextMinPrice)
        val editTextMaxPrice = findViewById<EditText>(R.id.editTextMaxPrice)

        // Initialize filters
        setupCategoriesSpinner(spinnerCategories)
        setupRatingsSpinner(spinnerRatings)

        // Load products from Firestore
        fetchProductsFromFirestore()

        // Handle category selection
        spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position) as String
                filterProducts(selectedCategory, null, null, null)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Handle rating selection
        spinnerRatings.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val minRating = when (position) {
                    1 -> 4.0
                    2 -> 3.0
                    3 -> 2.0
                    4 -> 1.0
                    else -> null
                }
                filterProducts(null, minRating, null, null)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Apply price filter
        findViewById<Button>(R.id.buttonApplyPriceFilter).setOnClickListener {
            val minPrice = editTextMinPrice.text.toString().toDoubleOrNull()
            val maxPrice = editTextMaxPrice.text.toString().toDoubleOrNull()
            filterProducts(null, null, minPrice, maxPrice)
        }
    }

    private fun setupCategoriesSpinner(spinner: Spinner) {
        val categories = listOf("All", "Electronics", "Clothing", "Books", "Accessories", "Shoes") // Example categories
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupRatingsSpinner(spinner: Spinner) {
        val ratings = listOf("All", "4+", "3+", "2+", "1+")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ratings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun fetchProductsFromFirestore() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                allProducts.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    allProducts.add(product)
                }
                filteredProducts = allProducts.toMutableList() // Initially, show all products
                groupAndDisplayProducts()  // Update UI with all products (no filtering)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterProducts(category: String?, minRating: Double?, minPrice: Double?, maxPrice: Double?) {
        filteredProducts = allProducts.filter {
            (category == null || category == "All" || it.category == category) &&
                    (minRating == null || it.rating >= minRating) &&
                    (minPrice == null || it.price >= minPrice) &&
                    (maxPrice == null || it.price <= maxPrice)
        }.toMutableList()
        groupAndDisplayProducts()
    }

    private fun groupAndDisplayProducts() {
        val sections = filteredProducts.groupBy { it.category }
            .map { (category, products) -> Section(category, products) }

        sectionedAdapter = SectionedProductAdapter(sections) { product ->
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }
        recyclerView.adapter = sectionedAdapter
    }

}


