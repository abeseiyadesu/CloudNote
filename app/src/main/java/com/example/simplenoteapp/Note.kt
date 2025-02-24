package com.example.simplenoteapp

data class Note(
    val noteId: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = 0
)
