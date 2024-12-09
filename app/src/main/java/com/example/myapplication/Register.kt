package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val etFirstName: EditText = findViewById(R.id.etFirstName)
        val etLastName: EditText = findViewById(R.id.etLastName)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etConfirmPassword: EditText = findViewById(R.id.etConfirmPassword)
        val rgSex: RadioGroup = findViewById(R.id.rgSex)
        val etCity: EditText = findViewById(R.id.etCity)
        val btnRegister: Button = findViewById(R.id.btnRegister)

        // Register button click listener
        btnRegister.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val sexId = rgSex.checkedRadioButtonId
            val sex = findViewById<RadioButton>(sexId)?.text.toString()
            val city = etCity.text.toString().trim()

            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || sex.isEmpty() || city.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication: Register the user
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Successfully created user in Firebase Auth
                        val user = auth.currentUser

                        // Store additional user information in Firestore
                        val userData = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "sex" to sex,
                            "city" to city
                        )

                        firestore.collection("users").document(user!!.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                // Successfully saved to Firestore
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                // Navigate to another activity, e.g., HomeActivity
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish() // Close the Register activity
                            }
                            .addOnFailureListener { exception ->
                                // Error saving user data to Firestore
                                Toast.makeText(this, "Error saving data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        // Registration failed
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
