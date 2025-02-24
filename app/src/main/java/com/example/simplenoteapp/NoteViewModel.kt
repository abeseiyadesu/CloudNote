package com.example.simplenoteapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID

// Composeの画面からメモを保存させたいため必要
class NoteViewModel : ViewModel() {
    private val repository = NoteRepository()

    // メモを保存
    fun saveNote(title: String, content: String) {
        val noteId= UUID.randomUUID().toString()
        val note=Note(
            noteId = noteId,
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch{
            repository.saveNote(note)
        }
    }
}