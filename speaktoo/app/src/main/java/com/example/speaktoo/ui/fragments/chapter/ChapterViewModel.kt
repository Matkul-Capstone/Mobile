package com.example.speaktoo.ui.fragments.chapter

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speaktoo.data.chapter.ChapterItem
import com.example.speaktoo.data.chapter.ChapterHeader
import com.example.speaktoo.data.chapter.ChapterJson
import com.example.speaktoo.data.models.Sentence
import com.example.speaktoo.data.repository.SentencesRepository
import com.example.speaktoo.utils.loadJsonFromAssets
import com.example.speaktoo.utils.parseJsonToChapters
import kotlinx.coroutines.launch

class ChapterViewModel(application: Application) : AndroidViewModel(application) {
    private val _levelName = MutableLiveData<String>("Beginner")
    val levelName: LiveData<String> get() = _levelName

    private val _chaptersJson = MutableLiveData<List<ChapterJson>>()
    private val chaptersJson: LiveData<List<ChapterJson>> get() = _chaptersJson

    private val _chapters = MutableLiveData<List<ChapterHeader>>()
    val chapters: LiveData<List<ChapterHeader>> get() = _chapters

    fun loadChapters(levelName: String) {
        val json = loadJsonFromAssets(getApplication(), "$levelName.json")
        if (json != null) {
            val rawChapters = parseJsonToChapters(json)

            val groupedChapters = rawChapters.groupBy { it.chapter }

            var chapterCounter = 1

            val formattedChapters = groupedChapters.map { entry ->
                ChapterHeader(
                    title = "Chapter ${chapterCounter++}",
                    description = entry.key,
                    items = entry.value.map {
                        ChapterItem(
                            sentenceId = it.sentence_id,
                            sentenceType = it.sentence_type,
                            sentence = it.sentence
                        )
                    }
                )
            }

            _chapters.postValue(formattedChapters)
        } else {
            _chapters.postValue(emptyList())
        }
    }
}