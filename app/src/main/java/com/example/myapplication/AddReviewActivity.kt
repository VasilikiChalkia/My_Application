package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddReviewActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        val productId = intent.extras?.getString("productId")?.toIntOrNull()
        val user = auth.currentUser // Get the current authenticated user

        if (user == null) {
            Toast.makeText(this, "You must be signed in to submit a review", Toast.LENGTH_SHORT)
                .show()
            finish() // Exit activity if the user is not authenticated
            return
        }

        val userId = user.uid // Get the unique user ID
        if (productId == null) {
            Toast.makeText(this, "Product ID not received", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val buttonSubmitReview = findViewById<Button>(R.id.buttonSubmitReview)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val editTextComment = findViewById<EditText>(R.id.editTextComment)

        buttonSubmitReview.setOnClickListener {
            val rating = ratingBar.rating
            val comment = editTextComment.text.toString()

            if (rating > 0 && comment.isNotEmpty()) {
                submitReview(userId, productId, rating, comment)
            } else {
                Toast.makeText(this, "Please provide a rating and comment", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun submitReview(userId: String, productId: Int, rating: Float, comment: String) {
        db.collection("products")
            .whereEqualTo("id", productId) // Query for the document with the matching `id`
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0] // Get the first matching document
                    val documentId = document.id // Get the document ID

                    // Retrieve the current product data
                    val currentRating = document.getDouble("rating") ?: 0.0
                    val totalReviews = document.getLong("totalReviews") ?: 0L

                    // Calculate the new average rating
                    val newTotalReviews = totalReviews + 1
                    val newAverageRating =
                        ((currentRating * totalReviews) + rating) / newTotalReviews

                    val review = mapOf(
                        "userId" to userId,
                        "rating" to rating,
                        "comment" to comment,
                        "timestamp" to System.currentTimeMillis()
                    )

                    // Update the product's rating and add the review
                    db.collection("products")
                        .document(documentId)
                        .update(
                            mapOf(
                                "rating" to newAverageRating,          // Update the average rating
                                "totalReviews" to newTotalReviews,    // Increment the total reviews count
                                "reviews" to FieldValue.arrayUnion(review) // Add the new review
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Review submitted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e("AddReviewActivity", "Error adding review", e)
                            Toast.makeText(
                                this,
                                "Failed to submit review: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(this, "Product not found. Please try again.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("AddReviewActivity", "Error checking product document", e)
                Toast.makeText(
                    this,
                    "Error checking product document: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
