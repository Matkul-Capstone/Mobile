package com.example.speaktoo.ui.milestone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.speaktoo.databinding.FragmentMilestoneBinding

class MilestoneFragment : Fragment() {

    private var _binding: FragmentMilestoneBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val notificationsViewModel =
//            ViewModelProvider(this).get(MilestoneViewModel::class.java)

        _binding = FragmentMilestoneBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textMilestone
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}