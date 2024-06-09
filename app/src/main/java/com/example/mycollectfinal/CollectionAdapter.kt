package com.example.mycollectfinal

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CollectionAdapter(options: FirestoreRecyclerOptions<Collection>, private val onItemClicked: (Collection) -> Unit) :
    FirestoreRecyclerAdapter<Collection, CollectionAdapter.CollectionViewHolder>(options) {

    class CollectionViewHolder(itemView: View, onItemClicked: (Collection) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val collectionImage = itemView.findViewById<ImageView>(R.id.collectionImage)
        val collectionName = itemView.findViewById<TextView>(R.id.collectionName)
        val progressBar  = itemView.findViewById<ProgressBar>(R.id.progressBar)
        val progressText = itemView.findViewById<TextView>(R.id.progressText)

        fun bind(collection: Collection, onItemClicked: (Collection) -> Unit) {
            itemView.setOnClickListener {
                onItemClicked(collection)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_item, parent, false)
        return CollectionViewHolder(itemView, onItemClicked)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int, model: Collection) {
        // Load image into ImageView using a different method
        holder.collectionImage.setImageResource(R.drawable.splash)

        // Set other views
        holder.collectionName.text = model.name
        fetchItemCount(model.name) { itemCount ->
            val progress = calculateProgress(itemCount, model.goalAmount)
            holder.progressBar.progress = progress
            holder.progressText.text = "$itemCount / ${model.goalAmount}"
        }

        // Handle item click
        holder.bind(model, onItemClicked)
    }

    private fun calculateProgress(itemCount: Int, goalAmount: Int): Int {
        return if (goalAmount == 0) 0 else (itemCount * 100 / goalAmount)
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
}
