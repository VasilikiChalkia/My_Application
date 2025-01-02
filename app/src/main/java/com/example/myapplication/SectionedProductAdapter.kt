package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Adapter for a RecyclerView that displays products grouped by category
class SectionedProductAdapter(
    private var groupedProducts: Map<String, List<Product>>, // Products grouped by category
    private val onProductClick: (Product) -> Unit // Click handler for product items
) : RecyclerView.Adapter<SectionedProductAdapter.CategoryViewHolder>() {

    // Returns the number of categories
    override fun getItemCount(): Int = groupedProducts.size

    // Create a ViewHolder for each category
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    // Bind the data (category name and products) to the ViewHolder
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryName = groupedProducts.keys.elementAt(position) // Get category name by position
        val products = groupedProducts[categoryName] ?: emptyList() // Get products for this category

        holder.categoryName.text = categoryName // Set the category name

        // Set up the RecyclerView for displaying products within this category
        val productAdapter = ProductAdapter(products, onProductClick) // Pass the click handler
        holder.recyclerView.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(holder.itemView.context, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    // Update the grouped products and refresh the RecyclerView
    fun updateGroupedProducts(newGroupedProducts: Map<String, List<Product>>) {
        groupedProducts = newGroupedProducts
        notifyDataSetChanged() // Notify the adapter to refresh the UI
    }

    // ViewHolder class to hold the category name and the RecyclerView for products
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.textViewCategoryName) // TextView for category name
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerViewCategoryProducts) // RecyclerView for products
    }
}
