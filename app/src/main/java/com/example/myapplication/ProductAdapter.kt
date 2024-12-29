package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(
    private val productList: List<Product>,
    private val onItemClicked: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewProduct)
        val textName: TextView = itemView.findViewById(R.id.textViewProductName)
        val textPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val textRating: TextView = itemView.findViewById(R.id.textViewProductRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Use Glide to load the image from the URL
        Glide.with(holder.itemView.context)
            .load(product.imageResId) // The product image URL
            .into(holder.imageView)

        holder.textName.text = product.name
        holder.textPrice.text = "$${product.price}"
        holder.textRating.text = "⭐ ${product.rating}"

        holder.itemView.setOnClickListener { onItemClicked(product) }
    }

    override fun getItemCount() = productList.size
}