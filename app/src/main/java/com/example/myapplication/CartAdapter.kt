package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemCartBinding

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onItemRemoved: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val cartItems = mutableListOf<CartItem>()

    fun submitList(newCartItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newCartItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount() = cartItems.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textProductName: TextView = itemView.findViewById(R.id.textProductName)
        private val textQuantity: TextView = itemView.findViewById(R.id.textQuantity)
        private val textPrice: TextView = itemView.findViewById(R.id.textPrice)
        private val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)
        private val buttonAdd: Button = itemView.findViewById(R.id.buttonAdd)

        fun bind(cartItem: CartItem) {
            textProductName.text = cartItem.product.name
            textQuantity.text = "Quantity: ${cartItem.quantity}"
            textPrice.text = "Price: $${cartItem.product.price * cartItem.quantity}"

            buttonRemove.setOnClickListener {
                val newQuantity = cartItem.quantity - 1
                if (newQuantity > 0) {
                    onQuantityChanged(cartItem, newQuantity)
                } else {
                    onItemRemoved(cartItem)
                }
            }

            buttonAdd.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                onQuantityChanged(cartItem, newQuantity)
            }
        }
    }
}



