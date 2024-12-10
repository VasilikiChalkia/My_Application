package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Glide import for loading images

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onRemoveClicked: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewCartProduct)
        val textName: TextView = itemView.findViewById(R.id.textViewCartProductName)
        val textPrice: TextView = itemView.findViewById(R.id.textViewCartProductPrice)
        val textQuantity: TextView = itemView.findViewById(R.id.textViewCartProductQuantity)
        val buttonRemove: Button = itemView.findViewById(R.id.buttonRemoveFromCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val imageUrl = cartItem.product.imageResId // Assuming this is a URL (String)

        // Use Glide to load the image from the URL
        Glide.with(holder.imageView.context)
            .load(imageUrl) // The image URL from Firestore
            .into(holder.imageView)

        holder.textName.text = cartItem.product.name
        holder.textPrice.text = "$${cartItem.product.price}"
        holder.textQuantity.text = "Quantity: ${cartItem.quantity}"

        holder.buttonRemove.setOnClickListener { onRemoveClicked(cartItem) }
    }

    override fun getItemCount() = cartItems.size
}
