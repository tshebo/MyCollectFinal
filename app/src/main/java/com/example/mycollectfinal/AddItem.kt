package com.example.mycollectfinal

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddItem : AppCompatActivity() {

    private lateinit var conditionSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val itemName = findViewById<EditText>(R.id.itemName)
        val description = findViewById<EditText>(R.id.description)
        val quantity = findViewById<EditText>(R.id.quantity)
        val itemImg = findViewById<ImageView>(R.id.itemImg)
        val addItemButton = findViewById<Button>(R.id.addItem)
        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
        conditionSpinner = findViewById(R.id.conditionSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.condition_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            conditionSpinner.adapter = adapter
        }

        val collectionName = intent.getStringExtra("collectionName") ?: "default_collection_name"

        addItemButton.setOnClickListener {
            val name = itemName.text.toString().trim()
            val desc = description.text.toString().trim()
            val cond = conditionSpinner.selectedItem.toString()
            val qty = quantity.text.toString().trim().toIntOrNull() ?: 0
            val imageUri = null

            if (name.isNotEmpty() && desc.isNotEmpty() && cond.isNotEmpty() && qty > 0) {
                val newItem = Item(name, desc, cond, Timestamp.now(), qty, imageUri)
                addItemToFirestore(newItem, collectionName) { success ->
                    if (success) {
                        Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Home::class.java))
                    } else {
                        Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addItemToFirestore(item: Item, collectionName: String, onComplete: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocument = db.collection("users").document(currentUser.email!!)
            val itemId = db.collection("users").document().id
            val itemWithId = item.copy(itemId = itemId)
            userDocument.collection("collections").document(collectionName).collection("items").document(itemId).set(itemWithId)
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    onComplete(false)
                }
        } else {
            onComplete(false)
        }
    }

}
