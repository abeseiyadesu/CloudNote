package com.example.simplenoteapp.ui.data

// Firestoreに保存するメモのデータ構造
data class Note(
    val noteId: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = 0
)
