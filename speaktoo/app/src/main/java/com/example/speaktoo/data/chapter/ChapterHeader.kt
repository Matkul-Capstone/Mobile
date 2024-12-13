package com.example.speaktoo.data.chapter

data class ChapterHeader(
    var title: String,
    var description: String,
    var items: List<ChapterItem>
)