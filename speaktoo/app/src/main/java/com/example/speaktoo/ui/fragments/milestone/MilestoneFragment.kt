package com.example.speaktoo.ui.fragments.milestone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speaktoo.databinding.FragmentMilestoneBinding
import com.example.speaktoo.ui.adapter.HistoryAdapter
import com.example.speaktoo.utils.UserPreferences
import com.example.speaktoo.ui.fragments.milestone.MilestoneViewModel

class MilestoneFragment : Fragment() {

    private var _binding: FragmentMilestoneBinding? = null
    private lateinit var milestoneViewModel: MilestoneViewModel

    private val binding get() = _binding!!

    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMilestoneBinding.inflate(inflater, container, false)
        val root: View = binding.root

        milestoneViewModel = ViewModelProvider(this).get(MilestoneViewModel::class.java)

        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser()

        user?.let {
            binding.textUserName.text = it.username
            binding.scoreBeginner.text = formatScore(it.beginner_score)
            binding.scoreIntermediate.text = formatScore(it.intermediate_score)
            binding.scoreAdvance.text = formatScore(it.advance_score)

            milestoneViewModel.fetchAllLogsForUser(it.user_id)
        }

        setupRecyclerViews()

        milestoneViewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            historyAdapter.submitList(historyList)
        }

        return root
    }

    private fun formatScore(score: Int): String {
        return "$score/1600"  // Format score as "score/1600"
    }

    private fun setupRecyclerViews() {
        historyAdapter = HistoryAdapter()
        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}