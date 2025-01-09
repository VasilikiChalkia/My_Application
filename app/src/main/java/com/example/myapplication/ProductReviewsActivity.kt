package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ProductReviewsActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var reviewsAdapter: ReviewsAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_reviews)

        val productId = intent.getStringExtra("productId") ?: return
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        reviewsAdapter = ReviewsAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.reviewsRecyclerView)
        recyclerView.adapter = reviewsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch reviews from the "reviews" subcollection under the specific product document
        db.collection("products")
            .document(productId)  // Access the product document
            .collection("reviews")  // Access the reviews subcollection
            .get()  // Fetch all reviews
            .addOnSuccessListener { querySnapshot ->
                val reviews = querySnapshot.toObjects(Review::class.java)
                reviewsAdapter.submitList(reviews)  // Update the RecyclerView with reviews
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load reviews: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

