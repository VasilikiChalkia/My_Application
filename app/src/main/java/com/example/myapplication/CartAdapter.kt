package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemCartBinding

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onItemRemoved: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = getItem(position)
        holder.bind(cartItem)
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.textViewProductName.text = cartItem.product.name
            binding.textViewProductPrice.text = "$${cartItem.product.price}"
            binding.editTextQuantity.setText(cartItem.quantity.toString())

            // Handle quantity changes
            binding.editTextQuantity.setOnEditorActionListener { _, _, _ ->
                val newQuantity = binding.editTextQuantity.text.toString().toIntOrNull()
                if (newQuantity != null && newQuantity > 0) {
                    onQuantityChanged(cartItem, newQuantity)
                }
                false
            }

            // Handle item removal
            binding.buttonRemove.setOnClickListener {
                onItemRemoved(cartItem)
            }
        }
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.product.id == newItem.product.id
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}
