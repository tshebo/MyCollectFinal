package com.example.mycollectfinal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.*

class AddCollection : AppCompatActivity() {

    private lateinit var collectionImage: ImageView
    private var imageUri: Uri? = null
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200
    private var isImageSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_collection)

        // Initialize views
        collectionImage = findViewById(R.id.collectionImage)
        val back = findViewById<ImageView>(R.id.backBtn)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val description = findViewById<TextView>(R.id.description)
        val category = findViewById<TextView>(R.id.category)
        val goalAmount = findViewById<TextView>(R.id.goalAmount)
        val createCollection = findViewById<Button>(R.id.createCollection)

        // Go back to the previous screen
        back.setOnClickListener { finish() }

        // Allow selecting an image
        collectionImage.setOnClickListener {
            showImagePickerDialog()
        }

        // Validations and collection creation
        createCollection.setOnClickListener {
            if (collectionName.text.isEmpty()) {
                collectionName.error = "This cannot be left blank"
                return@setOnClickListener
            } else if (description.text.isEmpty()) {
                description.error = "This cannot be left blank"
                return@setOnClickListener
            } else if (category.text.isEmpty()) {
                category.error = "This cannot be left blank"
                return@setOnClickListener
            } else if (goalAmount.text.isEmpty() || goalAmount.text.toString().toDouble() <= 0) {
                goalAmount.error = "This cannot be left blank or zero"
                return@setOnClickListener
            } else {
                val imageUrl = if (isImageSelected) {
                    // If an image is selected, use the captured image URI
                    imageUri.toString()
                } else {
                    // Use the default image URI
                    "android.resource://${packageName}/${R.drawable.splash}"
                }
                saveToFirestore(
                    collectionName.text.toString(),
                    description.text.toString(),
                    category.text.toString(),
                    imageUrl,
                    goalAmount.text.toString().toInt()
                )
            }
        }
    }

    // Function to save collection to Firestore
    private fun saveToFirestore(
        name: String,
        description: String,
        category: String,
        imageUrl: String,
        goalAmount: Int
    ) {
        // Get the current user's email
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        // Ensure the user is logged in
        if (userEmail != null) {
            val collection = Collection(
                name,
                description,
                category,
                imageUrl,
                goalAmount,
                Timestamp.now()
            )

            val db = FirebaseFirestore.getInstance()
            val collectionRef = db.collection("users").document(userEmail)
                .collection("collections").document(name)


            collectionRef.set(collection)
                .addOnSuccessListener {
                    Toast.makeText(this, "Collection created successfully", Toast.LENGTH_SHORT)
                        .show()

                    startActivity(Intent(this, Home::class.java))
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Error adding collection: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            // If user is not logged in, display an error message or redirect to login screen
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, LogIn::class.java))

        }
    }


    // Show a dialog to choose between taking a picture or selecting from the gallery
    private fun showImagePickerDialog() {
        val options = arrayOf("Take a Picture", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // Check for camera permission and open camera if granted
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_REQUEST_CODE
                        )
                    } else {
                        openCamera()
                    }
                }

                1 -> {
                    // Check for gallery permission and open gallery if granted
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            GALLERY_REQUEST_CODE
                        )
                    } else {
                        openGallery()
                    }
                }
            }
        }
        builder.show()
    }

    // Open the camera app to take a picture
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    // Open the gallery to select an image
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else if (requestCode == GALLERY_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the result of the camera or gallery activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    // Handle the camera image result
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    collectionImage.setImageBitmap(imageBitmap)
                }

                GALLERY_REQUEST_CODE -> {
                    // Handle the gallery image result
                    val imageUri: Uri? = data?.data
                    collectionImage.setImageURI(imageUri)
                }
            }
        }
    }
}