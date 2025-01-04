package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Adapter for a RecyclerView that displays products grouped by category
class SectionedProductAdapter(
    private val sections: List<Section>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SECTION_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category, parent, false)
                CategoryViewHolder(view)
            }
            PRODUCT_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product, parent, false)
                ProductViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var currentPosition = position
        for (section in sections) {
            if (currentPosition == 0) {
                (holder as CategoryViewHolder).categoryName.text = section.category
                return
            }
            currentPosition--

            if (currentPosition < section.products.size) {
                val product = section.products[currentPosition]
                val productHolder = holder as ProductViewHolder

                productHolder.productName.text = product.name
                productHolder.productPrice.text = "$${product.price}"
                productHolder.productRating.text = "Rating: ${String.format("%.1f", product.rating)}"

                // Load the image using Glide (or similar library)
                Glide.with(productHolder.itemView.context)
                    .load(product.imageResId) // Replace with the actual URL or resource ID
                    .into(productHolder.productImage)

                productHolder.itemView.setOnClickListener { onProductClick(product) }
                return
            }

            currentPosition -= section.products.size
        }
    }

    override fun getItemCount(): Int {
        return sections.sumOf { 1 + it.products.size }
    }

    override fun getItemViewType(position: Int): Int {
        var currentPosition = position
        for (section in sections) {
            if (currentPosition == 0) return SECTION_VIEW_TYPE
            currentPosition--
            if (currentPosition < section.products.size) return PRODUCT_VIEW_TYPE
            currentPosition -= section.products.size
        }
        throw IllegalArgumentException("Invalid position")
    }

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.textViewCategoryName)
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.textViewProductName)
        val productPrice: TextView = view.findViewById(R.id.textViewProductPrice)
        val productRating: TextView = view.findViewById(R.id.textViewProductRating)
        val productImage: ImageView = view.findViewById(R.id.imageViewProduct)
    }

    companion object {
        const val SECTION_VIEW_TYPE = 0
        const val PRODUCT_VIEW_TYPE = 1
    }
}