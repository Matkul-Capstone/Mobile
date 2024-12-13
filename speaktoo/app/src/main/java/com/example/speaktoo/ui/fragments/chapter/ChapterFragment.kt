package com.example.speaktoo.ui.fragments.chapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speaktoo.data.repository.SentencesRepository
import com.example.speaktoo.databinding.FragmentChapterBinding
import com.example.speaktoo.ui.adapter.ChapterAdapter
import kotlinx.coroutines.launch

class ChapterFragment : Fragment() {

    private var _binding: FragmentChapterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChapterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChapterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val levelName = getLevelName()

        // Set up the RecyclerView with an empty adapter initially
        val adapter = ChapterAdapter(emptyList(), requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Observe changes in chapters LiveData
        viewModel.chapters.observe(viewLifecycleOwner) { chapters ->
            adapter.updateChapters(chapters)
        }

        // Set the chapter title and load data
        binding.titleChapter.text = levelName
        viewModel.loadChapters(levelName)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Nullify binding to avoid memory leaks
    }

    private fun getLevelName(): String {
        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("level_name", "Beginner") ?: "Beginner"
    }
}
