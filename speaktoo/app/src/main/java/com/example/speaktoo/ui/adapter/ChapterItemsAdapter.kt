package com.example.speaktoo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speaktoo.databinding.CardChapterItemBinding
import com.example.speaktoo.data.chapter.ChapterItem
import java.util.Locale

class ChapterItemsAdapter(
    private val items: List<ChapterItem>,
    private val onItemClick: (ChapterItem) -> Unit
) : RecyclerView.Adapter<ChapterItemsAdapter.ChapterItemViewHolder>() {

    // Inflate the layout for chapter items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterItemViewHolder {
        val binding = CardChapterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChapterItemViewHolder(binding)
    }

    // Bind data to the ChapterItemViewHolder
    override fun onBindViewHolder(holder: ChapterItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // Return the total number of items in the chapter
    override fun getItemCount(): Int = items.size

    // Inner ViewHolder class for individual chapter items
    inner class ChapterItemViewHolder(private val binding: CardChapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChapterItem) {
            // Correct formatting of sentence ID
            binding.itemNumber.text = String.format(locale = Locale.getDefault(), "%d", item.sentenceId)

            // Set up the click listener
            binding.root.setOnClickListener {
                onItemClick(item) // Pass the clicked item to the listener
            }
        }
    }
}