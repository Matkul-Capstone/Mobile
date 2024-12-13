package com.example.speaktoo.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.speaktoo.data.repository.LogsRepository
import com.example.speaktoo.ui.activity.newusername.NewUsernameActivity
import com.example.speaktoo.ui.activity.newpassword.ResetPasswordActivity
import com.example.speaktoo.databinding.FragmentProfileBinding
import com.example.speaktoo.utils.UserPreferences
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser()
        if (user != null) profileViewModel.updateUser(user)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.username.text = user.username
                binding.email.text = user.user_email
            } else {
                binding.username.text = "Guest"
                binding.email.text = "Not logged in"
            }
        }

        setupButtonListeners()

        return root
    }

    private fun toResetPassword() {
        val intent = Intent(requireContext(), ResetPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun toNewUsername() {
        val intent = Intent(requireContext(), NewUsernameActivity::class.java)
        startActivity(intent)
    }

    private fun setupButtonListeners() {
        val buttonChangePassword = binding.changePasswordButton
        buttonChangePassword.setOnClickListener { toResetPassword() }

        val buttonChangeUsername = binding.changeUsernameButton
        buttonChangeUsername.setOnClickListener { toNewUsername() }

        val buttonLogout = binding.logoutButton
        buttonLogout.setOnClickListener {
            lifecycleScope.launch {
                profileViewModel.user.value?.let {
                    val logsRepository = LogsRepository(requireContext())
                    logsRepository.deleteLogsByUserId(it.user_id)
                }
                val userPreferences = UserPreferences(requireContext())
                userPreferences.clearUser()
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}