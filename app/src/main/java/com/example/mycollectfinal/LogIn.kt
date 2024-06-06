package com.example.mycollectfinal

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LogIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)

        // Apply WindowInsetsListener to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Views
        val emailInfo = findViewById<EditText>(R.id.emailView1)
        val passwordInfo = findViewById<EditText>(R.id.passwordView1)
        val logInBtn = findViewById<Button>(R.id.logInBtn)
        val signUpLink = findViewById<TextView>(R.id.signUpLink)

        // Handle sign up link click
        signUpLink.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }

        // Handle login button click
        logInBtn.setOnClickListener {
            val email = emailInfo.text.toString()
            val password = passwordInfo.text.toString()

            // Validate email address
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInfo.setError("Email is invalid")
                return@setOnClickListener
            }

            // Validate password
            if (password.length < 8) {
                passwordInfo.setError("Password must be at least 8 characters long")
                return@setOnClickListener
            }

            // Perform Firebase login
            logInFirebase(email, password)
        }
    }

    private fun logInFirebase(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(this@LogIn, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed
                    val exception = task.exception
                    Toast.makeText(this@LogIn, "Email or Password is Incorrect", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
}
