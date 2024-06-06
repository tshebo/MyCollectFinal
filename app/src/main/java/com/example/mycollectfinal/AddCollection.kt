package com.example.mycollectfinal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AddCollection : AppCompatActivity() {

    private lateinit var collectionImage: ImageView
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200

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

        // Set up the back button to finish the activity and go back to the previous screen
        back.setOnClickListener { finish() }

        // Set up the collection image view to allow selecting an image
        collectionImage.setOnClickListener {
            showImagePickerDialog()
        }

        // Other validation and setup code can be added here
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
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                    } else {
                        openCamera()
                    }
                }
                1 -> {
                    // Check for gallery permission and open gallery if granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_REQUEST_CODE)
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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
