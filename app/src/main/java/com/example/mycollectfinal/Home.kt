package com.example.mycollectfinal

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Home : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize views
        recyclerView = findViewById(R.id.collectionList)
//        val menuBtn: ImageButton = findViewById(R.id.menuBtn)
        val addCollection: FloatingActionButton = findViewById(R.id.addCollection)
        val welcomeText: TextView = findViewById(R.id.welcomeText)
        val categoryContainer = findViewById<LinearLayout>(R.id.categoryContainer)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser

        // Display personalized welcome message
        currentUser?.let {
            val userDocument = db.collection("users").document(it.email.toString())
            userDocument.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fullName = document.getString("fullName") ?: "User"
                        welcomeText.text = "Welcome, $fullName"
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error fetching user data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            welcomeText.text = "Welcome, Guest"
        }

        // Navigate to adding a collection
        addCollection.setOnClickListener {
            startActivity(Intent(this, AddCollection::class.java))
        }

        // Navigate to menu
//        menuBtn.setOnClickListener { showMenu() }

        // Set up RecyclerView
        setupRecyclerView()
    }

    private fun showMenu() {
        // Implement menu functionality
    }

    private fun setupRecyclerView() {
        val currentUser = auth.currentUser

        currentUser?.let {
            val userDocument = db.collection("users").document(it.email.toString())
            val query: Query = userDocument.collection("collections")

            val options: FirestoreRecyclerOptions<Collection> =
                FirestoreRecyclerOptions.Builder<Collection>()
                    .setQuery(query, Collection::class.java)
                    .build()

            collectionAdapter = CollectionAdapter(options) { collection ->
                // Navigate to ViewSelectedCollection with the selected collection details
                val intent = Intent(this, ViewSelectedCollection::class.java)
                intent.putExtra("collectionName", collection.name)
                startActivity(intent)
            }
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = collectionAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        collectionAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        collectionAdapter.stopListening()
    }
}
