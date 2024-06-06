package com.example.mycollectfinal

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Home : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    //    private lateinit var viewAdapter: CollectionAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        welcomeText.text = "Welcome, ${currentUser?.email}"


        val addCollection: FloatingActionButton = findViewById(R.id.addCollection)
        addCollection.setOnClickListener {
            startActivity(Intent(this, AddCollection::class.java))
        }


    }




}
