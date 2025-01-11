package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemWishlistBinding


class WishlistAdapter(
    private val onQuantityChanged: (WishlistItem, Int) -> Unit,
    private val onItemRemoved: (WishlistItem) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    private val wishlistItems = mutableListOf<WishlistItem>()

    fun submitList(newWishlistItems: List<WishlistItem>) {
        wishlistItems.clear()
        wishlistItems.addAll(newWishlistItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wishlist, parent, false)
        return WishlistViewHolder(view)
    }



    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val wishlistItem = wishlistItems[position]
        holder.bind(wishlistItem)
    }


    override fun getItemCount() = wishlistItems.size

    inner class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textProductName: TextView = itemView.findViewById(R.id.textProductName)
        private val textQuantity: TextView = itemView.findViewById(R.id.textQuantity)
        private val textPrice: TextView = itemView.findViewById(R.id.textPrice)
        private val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)
        private val buttonAdd: Button = itemView.findViewById(R.id.buttonAdd)

        fun bind(wishlistItem: WishlistItem) {
            textProductName.text = wishlistItem.product.name
            textQuantity.text = "Quantity: ${wishlistItem.quantity}"
            textPrice.text = "Price: $${wishlistItem.product.price * wishlistItem.quantity}"

            buttonRemove.setOnClickListener {
                val newQuantity = wishlistItem.quantity - 1
                if (newQuantity > 0) {
                    onQuantityChanged(wishlistItem, newQuantity) // Update quantity locally and in Firestore
                } else {
                    onItemRemoved(wishlistItem) // Remove item from Firestore
                }
            }

            buttonAdd.setOnClickListener {
                val newQuantity = wishlistItem.quantity + 1
                onQuantityChanged(wishlistItem, newQuantity)
            }


        buttonAdd.setOnClickListener {
                val newQuantity = wishlistItem.quantity + 1
                onQuantityChanged(wishlistItem, newQuantity)
            }


        }
    }
}
