package com.example.speaktoo.ui.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.speaktoo.ui.adapter.LevelAdapter
import com.example.speaktoo.R
import com.example.speaktoo.api.model.user.ChangeUserTypeResponse
import com.example.speaktoo.api.model.user.UserData
import com.example.speaktoo.databinding.FragmentHomeBinding
import com.example.speaktoo.utils.UserPreferences

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser()

        if (user != null) {
            binding.textUsernameHome.text = formatUsernameWelcome(user.username)
            binding.textUserLevel.text = user.user_type

            if (user.user_type == null) {
                findNavController().navigate(R.id.navigation_questionnaire)
            }

            checkAndUpdateUserType(user)
        }

        binding.buttonContinueLearning.setOnClickListener {
            val userType = user?.user_type ?: "Beginner"
            toStartLevel(userType, requireContext())
        }

        val levelsGridView: GridView = binding.gridLevels

        viewModel.levels.observe(viewLifecycleOwner, Observer { levelsList ->
            val adapter = LevelAdapter(requireContext(), ArrayList(levelsList))
            levelsGridView.adapter = adapter
        })

        viewModel.loadLevels()

        setupObserver(userPreferences)

        levelsGridView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.levels.value?.let { levelsList ->
                    val userType = user?.user_type ?: "Beginner"
                    val selectedLevel = levelsList[position].level_name

                    if ((userType == "Beginner" && selectedLevel != "Beginner") ||
                        (userType == "Intermediate" && selectedLevel == "Advanced")) {
                        Toast.makeText(
                            requireContext(),
                            "This level is locked, please complete your current level.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        toStartLevel(selectedLevel, requireContext())
                    }
                }
            }

        return root
    }

    override fun onResume() {
        super.onResume()

        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser()

        if (user != null) {
            binding.textUsernameHome.text = formatUsernameWelcome(user.username)
            binding.textUserLevel.text = user.user_type

            // Check and update user type if necessary
            checkAndUpdateUserType(user)
        }

        viewModel.loadLevels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatUsernameWelcome(username: String): String {
        return "$username!"
    }

    private fun checkAndUpdateUserType(user: UserData) {
        val sharedPreferences = requireContext().getSharedPreferences("User Preferences", Context.MODE_PRIVATE)
        val completed = sharedPreferences.getInt("completed", 0)

        if (user.user_type == "Beginner" && user.beginner_score == 1600) {
            updateUserType(user.user_id, "Intermediate")
        } else if (user.user_type == "Intermediate" && user.intermediate_score == 1600) {
            updateUserType(user.user_id, "Advanced")
        } else if (user.user_type == "Advanced" && user.advance_score == 1600 && completed == 0) {
            toCompletedLevel()
        }
    }

    private fun updateUserType(userId: String, userType: String) {
        showLoading(true)
        viewModel.changeUserType(userId, userType)
    }

    private fun setupObserver(userPreferences: UserPreferences) {
        viewModel.changeUserTypeResponse.observe(viewLifecycleOwner) { response ->
            handleUpdateResponse(response, userPreferences)
        }
    }

    private fun handleUpdateResponse(response: ChangeUserTypeResponse, userPreferences: UserPreferences) {
        showLoading(false)
        if (response.success) {
            val user = userPreferences.getUser()
            if (user != null) {
                user.user_type = response.data?.userType ?: user.user_type
                userPreferences.saveUser(user)

                binding.textUserLevel.text = user.user_type

                toCompletedLevel()
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

    private fun toStartLevel(selectedLevel: String, context: Context) {
        viewModel.selectLevel(selectedLevel, context)
        findNavController().navigate(R.id.navigation_start_level)
    }

    private fun toCompletedLevel() {
        findNavController().navigate(R.id.navigation_completed_level,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true)
                .build()
        )
    }
}