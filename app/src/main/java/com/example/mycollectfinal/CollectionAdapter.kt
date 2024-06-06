package com.example.mycollectfinal

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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class CollectionAdapter(options: FirestoreRecyclerOptions<Collection>) :
    FirestoreRecyclerAdapter<Collection, CollectionAdapter.CollectionViewHolder>(options) {

    class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val collectionImage: ImageView = itemView.findViewById(R.id.collectionImage)
        val collectionName: TextView = itemView.findViewById(R.id.collectionName)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val progressText: TextView = itemView.findViewById(R.id.progressText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_item, parent, false)
        return CollectionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int, model: Collection) {
        holder.collectionImage.setImageURI(Uri.parse(model.imageUri))
        holder.collectionName.text = model.name
        // Fetch item count for this collection and update progress
        fetchItemCount(model.name) { itemCount ->
            val progress = calculateProgress(itemCount, model.goalAmount)
            holder.progressBar.progress = progress
            holder.progressText.text = "$itemCount / ${model.goalAmount}"
        }
    }

    private fun calculateProgress(itemCount: Int, goalAmount: Int): Int {
        return if (goalAmount == 0) 0 else (itemCount * 100 / goalAmount)
    }

    private fun fetchItemCount(collectionName: String, callback: (Int) -> Unit) {
        // Replace this with actual Firestore query to fetch item count for the collection
        val db = FirebaseFirestore.getInstance()
        val userDocument = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.email!!)
        userDocument.collection("collections").document(collectionName).collection("items").get()
            .addOnSuccessListener { documents ->
                callback(documents.size())
            }
            .addOnFailureListener { e ->
                // Handle the error
                callback(0)
            }
    }
}
