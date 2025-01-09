package com.example.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProductViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun addReview(productId: Int, rating: Float, comment: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val productIdString = productId.toString() // Convert to string for Firestore

        val review = Review(
            userId = userId,
            productId = productId,
            rating = rating,
            comment = comment
        )

        val productRef = db.collection("products").document(productIdString)

        db.runTransaction { transaction ->
            val reviewRef = productRef.collection("reviews").document()
            transaction.set(reviewRef, review)

            val snapshot = transaction.get(productRef)
            val currentRating = snapshot.getDouble("averageRating") ?: 0.0
            val currentTotalReviews = snapshot.getLong("totalReviews") ?: 0L

            val newTotalReviews = currentTotalReviews + 1
            val newAverageRating = ((currentRating * currentTotalReviews) + rating) / newTotalReviews

            transaction.update(productRef, mapOf(
                "averageRating" to newAverageRating,
                "totalReviews" to newTotalReviews
            ))
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun getReviews(productId: Int): LiveData<List<Review>> {
        val reviewsLiveData = MutableLiveData<List<Review>>()
        val productIdString = productId.toString() // Convert to string for Firestore

        db.collection("products")
            .document(productIdString)
            .collection("reviews")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("ProductViewModel", "Error fetching reviews", exception)
                    return@addSnapshotListener
                }

                val reviews = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Review::class.java)
                }
                reviewsLiveData.value = reviews ?: emptyList()
            }

        return reviewsLiveData
    }
}
