package com.example.speaktoo.ui.fragments.practice

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.speaktoo.R
import com.example.speaktoo.api.model.transcribe.TranscribeResponse
import com.example.speaktoo.api.model.user.ChangeScoreResponse
import com.example.speaktoo.data.models.Log
import com.example.speaktoo.data.repository.LogsRepository
import com.example.speaktoo.data.repository.SentencesRepository
import com.example.speaktoo.databinding.FragmentPracticeBinding
import com.example.speaktoo.utils.AudioRecorder
import com.example.speaktoo.utils.UserPreferences
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class PracticeFragment : Fragment() {

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!
    private val practiceViewModel: PracticeViewModel by viewModels()

    private var audioRecorder: AudioRecorder? = null
    private var isRecording = false
    private var recordingHandler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        setupObservers()
        setupListeners()
        return binding.root
    }

    private fun setupObservers() {
        practiceViewModel.sentenceId.observe(viewLifecycleOwner) { id ->
            displayNavButton(id)
        }

        // Observe practice title
        practiceViewModel.practiceTitle.observe(viewLifecycleOwner) { title ->
            binding.titlePractice.text = title
        }

        // Observe sentence text
        practiceViewModel.sentenceText.observe(viewLifecycleOwner) { sentence ->
            binding.sentenceText.text = sentence
        }

        // Observe transcribe response
        practiceViewModel.transcribeResponse.observe(viewLifecycleOwner) { response ->
            handleTranscribeResponse(response)
        }

        practiceViewModel.changeScoreResponse.observe(viewLifecycleOwner) { response ->
            handlePostScoreResponse(response)
        }
    }

    private fun setupListeners() {
        binding.buttonBack.setOnClickListener {
            showFeedback(false)
            navigateToSentence(isNext = false)
        }

        binding.buttonNext.setOnClickListener {
            showFeedback(false)
            navigateToSentence(isNext = true)
        }

        binding.buttonGenerateFeedback.setOnClickListener {
            sendAudioForTranscription()
        }

        binding.buttonMic.setOnClickListener {
            toggleRecording()
        }

        binding.sentenceButton.setOnClickListener {
            val sentence = practiceViewModel.sentenceText.value
            if (sentence.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Sentence is empty.", Toast.LENGTH_SHORT).show()
            } else {
                practiceViewModel.playAudio(sentence)
            }
        }
    }

    private fun displayNavButton(sentenceId: Int) {
        if (sentenceId == 16 || sentenceId == 32 || sentenceId == 48) {
            binding.buttonNext.visibility = View.GONE
        }
        else if (sentenceId == 1 || sentenceId == 17 || sentenceId == 33) {
            binding.buttonBack.visibility = View.GONE
        }
        else {
            binding.buttonNext.visibility = View.VISIBLE
            binding.buttonBack.visibility = View.VISIBLE
        }
    }

    private fun navigateToSentence(isNext: Boolean) {
        val sentencesRepository = SentencesRepository(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val currentId = sharedPreferences.getInt("sentenceId", 1)
        val nextId = if (isNext) currentId + 1 else currentId - 1
        practiceViewModel.getSentenceById(nextId, isNext, sentencesRepository)
    }

    private fun toggleRecording() {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        if (isRecording) return

        showRecordingHint(true)
        binding.buttonMic.setImageResource(R.drawable.image_stop)

        val audioFile = File(requireContext().cacheDir, "audio.wav")
        audioRecorder = AudioRecorder(audioFile, requireContext())
        audioRecorder?.startRecording()
        isRecording = true

        recordingHandler = Handler(Looper.getMainLooper())
        recordingHandler?.postDelayed({
            if (isRecording) {
                stopRecording()
                Toast.makeText(requireContext(), "Recording stopped after 15 seconds", Toast.LENGTH_SHORT).show()
            }
        }, 15000)
    }

    private fun stopRecording() {
        if (!isRecording) return

        showRecordingHint(false)
        binding.buttonMic.setImageResource(R.drawable.image_mic)
        audioRecorder?.stopRecording()
        isRecording = false
        recordingHandler?.removeCallbacksAndMessages(null)
    }

    private fun sendAudioForTranscription() {
        showLoading(true)
        val audioFile = File(requireContext().cacheDir, "audio.wav")
        if (audioFile.exists()) {
            val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            val userPreferences = UserPreferences(requireContext())
            val user = userPreferences.getUser()
            val userId = user?.user_id ?: "Default uid"
            val timestamp = getCurrentTimestamp()
            val sentence = practiceViewModel.sentenceText.value.orEmpty()
            val chapter = sharedPreferences.getString("chapterDesc", "Basic Greetings and Introductions") ?: "Basic Greetings and Introductions"

            practiceViewModel.transcribeAudio(
                sid = practiceViewModel.sentenceId.value ?: 0,
                chapter = chapter,
                uid = userId,
                sentence = sentence,
                timestamp = timestamp,
                audioFile = audioFile
            )
        } else {
            showLoading(false)
            Toast.makeText(requireContext(), "No audio file found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleTranscribeResponse(response: TranscribeResponse) {
        if (response.success) {
            response.data?.let { data ->
                if (data.score == 100) {
                    val sentencesRepository = SentencesRepository(requireContext())
                    updateScore(data.sid.toInt(), data.score, sentencesRepository)
                }

                binding.score.text = String.format(Locale.getDefault(), "%d", data.score)
                binding.correctWords.text = data.correct_words?.joinToString(", ") ?: "None"
                binding.misspelledWords.text = data.wrong_words?.joinToString(", ") ?: "None"

                setScoreColor(data.score)

                val logsRepository = LogsRepository(requireContext())
                val log = Log(
                    timestamp = data.timestamp,
                    user_id = data.uid,
                    sentence_id = data.sid.toInt(),
                    score = data.score,
                    completed = if (data.completed) 1 else 0,
                    chapter = data.chapter
                )
                practiceViewModel.postLog(logsRepository, log)
                deleteAudioFile()
                showFeedback(true)
            }
        } else {
            Toast.makeText(requireContext(), response.message ?: "Transcription failed", Toast.LENGTH_SHORT).show()
        }
        showLoading(false)
    }

    private fun setScoreColor(score: Int) {
        val color = when {
            score <= 50 -> requireContext().getColor(android.R.color.holo_red_dark)
            score <= 75 -> requireContext().getColor(android.R.color.holo_orange_dark)
            score == 100 -> requireContext().getColor(R.color.dark_green)
            else -> requireContext().getColor(android.R.color.black)
        }
        binding.score.setTextColor(color)
    }

    private fun getCurrentTimestamp(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(java.util.Date(currentTimeMillis))
    }

    private fun updateScore(sentenceId: Int, newScore: Int, sentencesRepository: SentencesRepository) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser() ?: run {
            Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
            return
        }

        val levelName = sharedPreferences.getString("levelName", "Beginner") ?: "Beginner"
        val completionStatus = sharedPreferences.getInt("completionStatus", 0)

        if (completionStatus == 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val completedSentences = practiceViewModel.getCompletedSentences(levelName, sentencesRepository)
                    val updatedScore = calculateScore(newScore, completedSentences)
                    sharedPreferences.edit().putInt("sentence_${sentenceId}_score", newScore).apply()

                    when (levelName) {
                        "Beginner" -> user.beginner_score = updatedScore
                        "Intermediate" -> user.intermediate_score = updatedScore
                        "Advanced" -> user.advance_score = updatedScore
                    }

                    userPreferences.saveUser(user)
                    practiceViewModel.changeSentenceStatus(sentenceId, sentencesRepository)

                    postScore(updatedScore)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error updating score: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun calculateScore(newScore: Int, completedSentences: Int): Int {
        return newScore + (completedSentences * 100)
    }

    private fun postScore(score: Int) {
        val userPreferences = UserPreferences(requireContext())
        val user = userPreferences.getUser() ?: run {
            Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = user.user_id

        val levelName = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            .getString("levelName", "Beginner") ?: "Beginner"

        practiceViewModel.postScore(userId, levelName, score)
    }

    private fun handlePostScoreResponse(response: ChangeScoreResponse) {
        if (!response.success) {
            Toast.makeText(requireContext(), response.message ?: "Transcription failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRecordingHint(show: Boolean) {
        binding.hintContainer.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.loading?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showFeedback(show: Boolean) {
        _binding?.feedbackTextContainer?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun deleteAudioFile() {
        val audioFile = File(requireContext().cacheDir, "audio.wav")
        if (audioFile.exists()) {
            val deleted = audioFile.delete()
            if (!deleted) {
                Toast.makeText(requireContext(), "Failed to delete audio file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        audioRecorder?.stopRecording()
        audioRecorder = null
        recordingHandler?.removeCallbacksAndMessages(null)
        recordingHandler = null
    }
}