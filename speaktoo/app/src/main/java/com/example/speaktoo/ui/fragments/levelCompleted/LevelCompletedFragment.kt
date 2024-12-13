package com.example.speaktoo.ui.fragments.levelCompleted

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.speaktoo.R
import com.example.speaktoo.api.model.sentences.SentencesResponse
import com.example.speaktoo.data.repository.SentencesRepository
import com.example.speaktoo.databinding.FragmentLevelCompletedBinding
import com.example.speaktoo.utils.UserPreferences
import kotlinx.coroutines.launch

class LevelCompletedFragment : Fragment() {

    private var _binding: FragmentLevelCompletedBinding? = null
    private val binding get() = _binding!!
    private val levelCompletedViewModel: LevelCompletedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLevelCompletedBinding.inflate(inflater, container, false)

        val levelImageView = binding.levelImage
        val levelCompletedTitle = binding.completedLevelTitle
        val levelCompletedText = binding.completedLevelText
        val levelTextView = binding.textUserLevel
        val startLearningButton = binding.startLearningButton

        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser()

        if (user != null) {
            when {
                user.user_type == "Intermediate" && user.intermediate_score == 0 -> {
                    levelCompletedTitle.text = "Beginner level complete!"
                    levelTextView.text = "Intermediate"
                    levelImageView.setImageResource(R.drawable.image_level_intermediate)
                }
                user.user_type == "Advanced" && user.advance_score == 0 -> {
                    levelCompletedTitle.text = "Intermediate level complete!"
                    levelTextView.text = "Advanced"
                    levelImageView.setImageResource(R.drawable.image_level_advance)
                }
                user.advance_score == 1600 -> {
                    levelCompletedTitle.text = "Congratulations!"
                    levelCompletedText.visibility = View.GONE
                    levelTextView.text = "You've finished the Advanced level and completed the course!"
                    startLearningButton.text = "View Your Milestones"
                    levelImageView.setImageResource(R.drawable.image_congratulation)
                }
            }
        }

        val sentencesRepository = SentencesRepository(requireContext())

        setupObserver(sentencesRepository)

        startLearningButton.setOnClickListener {
            if (user != null) {
                when (user.user_type) {
                    "Intermediate" -> {
                        getSentenceByType("Intermediate", user.user_id)
                    }
                    "Advanced" -> {
                        if (user.advance_score == 0) {
                            getSentenceByType("Advanced", user.user_id)
                        }
                        else {
                            toMilestoneFragment()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun getLevelName(): String {
        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("level_name", "Beginner") ?: "Beginner"
    }

    private fun setupObserver(sentencesRepository: SentencesRepository) {
        levelCompletedViewModel.sentencesResponse.observe(viewLifecycleOwner) { response ->
            handleSentencesResponse(response, sentencesRepository)
        }
    }

    private fun handleSentencesResponse(response: SentencesResponse, sentencesRepository: SentencesRepository) {
        if (response.success && response.data != null) {
            saveSentencesToLocalAndNavigate(response.data, sentencesRepository)
        } else {
            showLoading(false)
            Toast.makeText(requireContext(), "Error loading sentences", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSentencesToLocalAndNavigate(
        sentencesData: List<com.example.speaktoo.api.model.sentences.SentencesData>,
        sentencesRepository: SentencesRepository
    ) {
        lifecycleScope.launch {
            levelCompletedViewModel.saveSentencesToLocal(sentencesData, sentencesRepository)
        }
        toChapterFragment()
    }

    private fun getSentenceByType(type: String, uid: String) {
        showLoading(true)
        levelCompletedViewModel.getSentencesByType(type, uid)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toChapterFragment() {
        findNavController().navigate(R.id.navigation_chapter,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true)
                .build()
        )
    }

    private fun toMilestoneFragment() {
        val sharedPreferences = requireContext().getSharedPreferences("User Preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("completed", 1).apply()
        findNavController().navigate(R.id.navigation_milestone,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_completed_level, true)
                .build()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}