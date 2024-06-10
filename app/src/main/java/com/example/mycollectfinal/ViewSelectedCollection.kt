package com.example.mycollectfinal

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ViewSelectedCollection : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var collectionItemAdapter: CollectionItemAdapter
    private lateinit var collectionNameTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_selected_collection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        recyclerView = findViewById(R.id.collectionList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        collectionNameTextView = findViewById(R.id.collectionName)
        progressBar = findViewById(R.id.progressBar)
        val addItem = findViewById<FloatingActionButton>(R.id.addItem)
        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        val deleteBtn = findViewById<ImageButton>(R.id.deleteBtn)
        deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        // Retrieve collection name from intent extras
        val collectionName = intent.getStringExtra("collectionName")
        if (collectionName == null) {
            finish()
            return
        }

        // Set up FirestoreRecyclerOptions with the provided collection name
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val userDocument = db.collection("users").document(currentUser!!.email!!)
        val query = userDocument.collection("collections").document(collectionName).collection("items")

        val options = FirestoreRecyclerOptions.Builder<Item>()
            .setQuery(query, Item::class.java)
            .build()

        // Initialize CollectionItemAdapter
        collectionItemAdapter = CollectionItemAdapter(options) { item ->
            val intent = Intent(this, EditItem::class.java)
            intent.putExtra("collectionName", collectionName)
           intent.putExtra("itemId", item.itemId)
            startActivity(intent)
        }

        // Set CollectionItemAdapter to RecyclerView
        recyclerView.adapter = collectionItemAdapter

        // Set up Collection Name and Progress Bar
        collectionName.let { setupCollectionDetails(it) }

        backBtn.setOnClickListener {
            finish()
        }
        addItem.setOnClickListener {
            val intent = Intent(this, AddItem::class.java)
            intent.putExtra("collectionName", collectionName)
//            intent.putExtra("itemId", item.itemId)
            startActivity(intent)

        }
    }

    override fun onStart() {
        super.onStart()
        collectionItemAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        collectionItemAdapter.stopListening()
    }

    private fun setupCollectionDetails(collectionName: String) {
        // Fetch collection details from Firestore using the provided collection name
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val db = FirebaseFirestore.getInstance()
            val userDocument = db.collection("users").document(it.email!!)
            userDocument.collection("collections").document(collectionName).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val goalAmount = documentSnapshot.getLong("goalAmount") ?: 0

                        // Update collection name TextView
                        collectionNameTextView.text = collectionName


                        fetchItemCount(collectionName) { itemCount ->
                            val progress = calculateProgress(itemCount, goalAmount)
                            progressBar.progress = progress
                        }
                    }
                }
                .addOnFailureListener { exception ->

                }
        }
    }

    private fun calculateProgress(itemCount: Int, goalAmount: Long): Int {
        return if (goalAmount == 0L) 0 else (itemCount * 100 / goalAmount).toInt()
    }

    private fun fetchItemCount(collectionName: String, callback: (Int) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocument = db.collection("users").document(currentUser.email!!)
            userDocument.collection("collections").document(collectionName).collection("items").get()
                .addOnSuccessListener { documents ->
                    callback(documents.size())
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    callback(0)
                }
        } else {
            callback(0)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Collection")
            .setMessage("Are you sure you want to delete this collection?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteCollectionFromFirestore()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteCollectionFromFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val collectionName = intent.getStringExtra("collectionName") ?: return
        val db = FirebaseFirestore.getInstance()
        currentUser?.let {
            val userDocument = db.collection("users").document(it.email!!)
            userDocument.collection("collections").document(collectionName)
                .delete()
                .addOnSuccessListener {
                    startActivity(Intent(this, Home::class.java))
                }
                .addOnFailureListener { e ->
                }
        }
    }
}
