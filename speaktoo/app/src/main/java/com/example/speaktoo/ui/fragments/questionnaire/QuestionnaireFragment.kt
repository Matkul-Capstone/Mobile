package com.example.speaktoo.ui.fragments.questionnaire

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.speaktoo.R
import com.example.speaktoo.api.model.user.ChangeUserTypeResponse
import com.example.speaktoo.databinding.FragmentQuestionnaireBinding
import com.example.speaktoo.utils.UserPreferences

class QuestionnaireFragment : Fragment() {

    private var _binding: FragmentQuestionnaireBinding? = null
    private val binding get() = _binding!!

    private val questionnaireViewModel: QuestionnaireViewModel by viewModels()

    companion object {
        fun newInstance() = QuestionnaireFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionnaireBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser()
        val userId = user?.user_id ?: return root

        setupObserver(userPreferences)
        setupButtonListener(userId)

        return root
    }

    private fun setupObserver(userPreferences: UserPreferences) {
        questionnaireViewModel.changeUserTypeResponse.observe(viewLifecycleOwner) { response ->
            handleUpdateResponse(response, userPreferences)
        }
    }

    private fun setupButtonListener(userId: String) {
        binding.buttonBeginner.setOnClickListener {
            updateUserType(userId, "Beginner")
        }

        binding.buttonIntermediate.setOnClickListener {
            updateUserType(userId, "Intermediate")
        }

        binding.buttonAdvance.setOnClickListener {
            updateUserType(userId, "Advanced")
        }
    }

    private fun updateUserType(userId: String, userType: String) {
        showLoading(true)
        questionnaireViewModel.changeUserType(userId, userType)
    }

    private fun handleUpdateResponse(response: ChangeUserTypeResponse, userPreferences: UserPreferences) {
        showLoading(false)
        if (response.success) {
            val user = userPreferences.getUser()
            if (user != null) {
                user.user_type = response.data?.userType ?: user.user_type
                userPreferences.saveUser(user)

                // Back to home
                findNavController().popBackStack()
            }
        } else {
            Toast.makeText(
                requireContext(),
                response.message ?: "Failed to change user type.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}