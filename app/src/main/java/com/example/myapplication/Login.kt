package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up login button action
        setupLogin()

        // Set up the "Go to Register" button action
        setupGoToRegisterButton()
    }

    private fun setupLogin() {
        val email = "vasilikidelrey@gmail.com"  // Replace with user input or retrieve it from EditText
        val password = "12345678"    // Replace with user input or retrieve it from EditText

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            loginUser(email, password)
        }
    }

    private fun setupGoToRegisterButton() {
        // Go to RegisterActivity when button is clicked
        findViewById<Button>(R.id.btnGoToRegister).setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Authentication success
                    val user = auth.currentUser
                    if (user != null) {
                        Toast.makeText(this, "Welcome ${user.email}", Toast.LENGTH_SHORT).show()

                        // Navigate to HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // Close the LoginActivity
                    } else {
                        // If user is null for some reason, show an error
                        Toast.makeText(this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Authentication failed
                    Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
