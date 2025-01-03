package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryProductAdapter(
    private val categories: List<Section>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<CategoryProductAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val section = categories[position]
        holder.categoryName.text = section.category

        val productAdapter = ProductAdapter(section.products, onProductClick)
        holder.productRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.productRecyclerView.adapter = productAdapter
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.textViewCategoryName)
        val productRecyclerView: RecyclerView = view.findViewById(R.id.recyclerViewProductsInCategory)
    }
}
