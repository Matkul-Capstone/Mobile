package com.example.speaktoo.utils

import android.content.Context
import com.example.speaktoo.data.chapter.ChapterJson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

fun loadJsonFromAssets(context: Context, fileName: String): String? {
    return try {
        context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ex: IOException) {
        ex.printStackTrace()
        null
    }
}

fun parseJsonToChapters(jsonString: String): List<ChapterJson> {
    val gson = Gson()
    val type = object : TypeToken<List<ChapterJson>>() {}.type
    val rawChapters: List<ChapterJson> = gson.fromJson(jsonString, type)
    return rawChapters.map {
        it.copy(sentence_type = it.sentence_type ?: "Unknown") // Provide a default value for null cases
    }
}
