package com.example.mycollectfinal

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditItem : AppCompatActivity() {

    private lateinit var itemNameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var conditionSpinner: Spinner
    private lateinit var quantityEditText: EditText
    private lateinit var itemImageView: ImageView
    private lateinit var updateItemButton: Button
    private lateinit var deleteBtn: ImageButton

    private lateinit var collectionName: String
    private lateinit var itemId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        // Retrieve collection name and item ID from intent extras
        val intent = intent
        collectionName = intent.getStringExtra("collectionName") ?: run {
            Log.e("EditItem", "No collection name provided")
            finish()
            return
        }
        itemId = intent.getStringExtra("itemId") ?: run {
            Log.e("EditItem", "No item ID provided")
            finish()
            return
        }

        // Initialize views
        itemNameEditText = findViewById(R.id.itemName)
        descriptionEditText = findViewById(R.id.description)
        conditionSpinner = findViewById(R.id.conditionSpinner)
        quantityEditText = findViewById(R.id.quantity)
        itemImageView = findViewById(R.id.itemImg)
        updateItemButton = findViewById(R.id.updateItem)
        deleteBtn = findViewById(R.id.deleteBtn)

        // Fetch item details from Firestore and populate UI
        fetchItemDetails()

        // Set up update item button click listener
        updateItemButton.setOnClickListener {
            updateItemDetails()
        }

        // Set up delete button click listener
        deleteBtn.setOnClickListener {
            deleteItem()
        }

        // Set up back button click listener
        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener { finish() }
    }

    private fun fetchItemDetails() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val userDocument = db.collection("users").document(currentUser!!.email!!)
        val itemDocument = userDocument.collection("collections").document(collectionName).collection("items").document(itemId)

        itemDocument.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val item = documentSnapshot.toObject(Item::class.java)
                if (item != null) {
                    // Populate UI fields with item details
                    itemNameEditText.setText(item.name)
                    descriptionEditText.setText(item.description)

                    // Assuming you have an array adapter for conditionSpinner
                    val conditionArrayAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.condition_options,
                        android.R.layout.simple_spinner_item
                    )
                    conditionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    conditionSpinner.adapter = conditionArrayAdapter
                    val conditionPosition = conditionArrayAdapter.getPosition(item.condition)
                    conditionSpinner.setSelection(conditionPosition)
                    quantityEditText.setText(item.quantity.toString())
                    // Set default image path
                    itemImageView.setImageResource(R.drawable.splash)
                } else {
                    Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
                    Log.e("EditItem", "Item is null")
                }
            } else {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
                Log.e("EditItem", "Document does not exist")
            }
        }.addOnFailureListener { e ->
            Log.e("EditItem", "Error fetching item details: ${e.message}")
        }
    }

    private fun updateItemDetails() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val userDocument = db.collection("users").document(currentUser!!.email!!)

        // Check if item exists and itemId is not null
        if (itemId != null) {
            val itemDocument = userDocument.collection("collections").document(collectionName).collection("items").document(itemId!!)

            // Update item with the retained itemId
            val updatedItem = Item(
                itemId = itemId!!, // Retain the itemId
                name = itemNameEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                condition = conditionSpinner.selectedItem.toString(),
                quantity = quantityEditText.text.toString().toInt(),
                imageUri = "drawable/splash" // Set this to your default image path or handle accordingly
            )

            itemDocument.set(updatedItem)
                .addOnSuccessListener {
                    Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("EditItem", "Error updating item: ${e.message}")
                    Toast.makeText(this, "Error updating item: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle the case where item or itemId is null
            Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteItem() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val userDocument = db.collection("users").document(currentUser!!.email!!)
        val itemDocument = userDocument.collection("collections").document(collectionName).collection("items").document(itemId)

        itemDocument.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("EditItem", "Error deleting item: ${e.message}")
                Toast.makeText(this, "Error deleting item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
