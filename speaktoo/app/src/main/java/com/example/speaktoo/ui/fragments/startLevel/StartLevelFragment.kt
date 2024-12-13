package com.example.speaktoo.ui.fragments.startLevel

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.speaktoo.R
import com.example.speaktoo.api.model.sentences.SentencesResponse
import com.example.speaktoo.data.repository.SentencesRepository
import com.example.speaktoo.databinding.FragmentStartLevelBinding
import com.example.speaktoo.utils.UserPreferences
import kotlinx.coroutines.launch

class StartLevelFragment : Fragment() {

    private var _binding: FragmentStartLevelBinding? = null
    private val binding get() = _binding!!
    private val startLevelViewModel: StartLevelViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartLevelBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sentencesRepository = SentencesRepository(requireContext())

        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser()
        val userId = user?.user_id ?: "Default uid"

        val levelImageView = binding.levelImage
        val levelTextView = binding.textUserLevel
        val startLearningButton = binding.startLearningButton

        val levelName = getLevelName()

        startLevelViewModel.setLevelName(levelName)

        startLevelViewModel.levelName.observe(viewLifecycleOwner) { name ->
            levelTextView.text = name
            when (name) {
                "Beginner" -> levelImageView.setImageResource(R.drawable.image_level_beginner)
                "Intermediate" -> levelImageView.setImageResource(R.drawable.image_level_intermediate)
                "Advanced" -> levelImageView.setImageResource(R.drawable.image_level_advance)
            }
        }

        setupObserver(sentencesRepository)

        startLearningButton.setOnClickListener {
            getSentenceByType(levelName, userId)
        }

        return root
    }

    private fun getLevelName(): String {
        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("level_name", "Beginner") ?: "Beginner"
    }

    private fun setupObserver(sentencesRepository: SentencesRepository) {
        startLevelViewModel.sentencesResponse.observe(viewLifecycleOwner) { response ->
            handleSentencesResponse(response, sentencesRepository)
        }
    }

    private fun handleSentencesResponse(response: SentencesResponse, sentencesRepository: SentencesRepository) {
        if (response.success && response.data != null) {
            saveSentencesToLocalAndNavigate(response.data, sentencesRepository)
        }
        showLoading(false)
    }

    private fun saveSentencesToLocalAndNavigate(
        sentencesData: List<com.example.speaktoo.api.model.sentences.SentencesData>,
        sentencesRepository: SentencesRepository
    ) {
        lifecycleScope.launch {
            startLevelViewModel.saveSentencesToLocal(sentencesData, sentencesRepository)
        }
        toChapterFragment()
    }

    private fun getSentenceByType(type: String, uid: String) {
        showLoading(true)
        startLevelViewModel.getSentencesByType(type, uid)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toChapterFragment() {
        findNavController().navigate(R.id.navigation_chapter,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_start_level, true)
                .build()
        )
    }
}