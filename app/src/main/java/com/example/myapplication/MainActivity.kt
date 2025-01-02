package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Simplified UI: Just add products to the cart
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Button(
                        onClick = {
                            // Add a new product to the cart (example)
                            val newProduct = Product(
                                id = 1,
                                name = "New Product",
                                description = "This is a new product",
                                price = 29.99,
                                rating = 4.5,
                                category = "Electronics",
                                imageResId = "new_product_image"
                            )
                            cartViewModel.addToCart(newProduct)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Product to Cart")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        // Just showing a button to add products to the cart
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Button(
                onClick = {
                    // Add a new product to the cart (example)
                    val newProduct = Product(
                        id = 1,
                        name = "New Product",
                        description = "This is a new product",
                        price = 29.99,
                        rating = 4.5,
                        category = "Electronics",
                        imageResId = "new_product_image"
                    )
                    // Call ViewModel addToCart logic
                    CartViewModel().addToCart(newProduct)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Product to Cart")
            }
        }
    }
}