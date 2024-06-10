package com.example.mycollectfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp

class CollectionItemAdapter(
    options: FirestoreRecyclerOptions<Item>,
    private val onItemClicked: (Item) -> Unit
) : FirestoreRecyclerAdapter<Item, CollectionItemAdapter.CollectionItemViewHolder>(options) {

    class CollectionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val condition: TextView = itemView.findViewById(R.id.condition)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val image: ImageView = itemView.findViewById(R.id.collectionImage)

        fun bind(item: Item, onItemClicked: (Item) -> Unit) {
            itemView.setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preview, parent, false)
        return CollectionItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CollectionItemViewHolder, position: Int, model: Item) {
        holder.itemName.text = model.name.toString()
        holder.quantity.text = model.quantity.toString()
        holder.condition.text = model.condition.toString()
        holder.image.setImageResource(R.drawable.splash)
        holder.bind(model, onItemClicked)
    }
}
