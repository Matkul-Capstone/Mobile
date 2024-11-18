package com.example.speaktoo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.speaktoo.LevelAdapter
import com.example.speaktoo.LevelModel
import com.example.speaktoo.R
import com.example.speaktoo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the GridView
        val levelsGridView: GridView = binding.gridLevels

        // Prepare the data for the adapter
        val levelsList = ArrayList<LevelModel>()
        levelsList.add(LevelModel("Beginner", R.drawable.image_comment_1))
        levelsList.add(LevelModel("Intermediate", R.drawable.image_translate_1))
        levelsList.add(LevelModel("Advanced", R.drawable.image_translate_2))

        // Set up the adapter
        val adapter = LevelAdapter(requireContext(), levelsList)
        levelsGridView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
