package com.example.speaktoo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.speaktoo.R
import com.example.speaktoo.databinding.CardChapterHeaderBinding
import com.example.speaktoo.data.chapter.ChapterHeader

class ChapterAdapter(
    private var chapters: List<ChapterHeader>,
    private val context: Context
) : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    // Function to update the list of chapters
    fun updateChapters(newChapters: List<ChapterHeader>) {
        chapters = newChapters
        notifyDataSetChanged()
    }

    // Inflate the layout for the chapter header
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = CardChapterHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChapterViewHolder(binding)
    }

    // Bind data to the ChapterViewHolder
    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(chapters[position])
    }

    // Return the total number of chapters
    override fun getItemCount(): Int = chapters.size

    // Inner ViewHolder class for chapter headers
    inner class ChapterViewHolder(private val binding: CardChapterHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chapter: ChapterHeader) {
            // Bind chapter title and description
            binding.chapterTitle.text = chapter.title
            binding.chapterDescription.text = chapter.description

            // Setup RecyclerView for chapter items
            val itemsAdapter = ChapterItemsAdapter(chapter.items) { item ->
                selectedItem(item.sentenceId, item.sentence, chapter.title, chapter.description)
                itemView.findNavController().navigate(R.id.navigation_practice)
            }
            binding.childRecyclerView.layoutManager = GridLayoutManager(binding.root.context, 4)
            binding.childRecyclerView.adapter = itemsAdapter
        }
    }

    fun selectedItem(sentenceId: Int, sentence: String, chapterTitle: String, chapterDesc: String) {
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt("sentenceId", sentenceId)
        editor.putString("sentence", sentence)
        editor.putString("chapterTitle", chapterTitle)
        editor.putString("chapterDesc", chapterDesc)

        editor.apply()
    }
}
