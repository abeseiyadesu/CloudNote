package com.example.simplenoteapp.ui.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// Composeの画面からメモを保存させたいため必要
class NoteViewModel : ViewModel() {
    private val repository = NoteRepository()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        // アプリ起動時にデータをロード
        loadNotes()
    }

    private fun loadNotes() {
        repository.getNotes { notesList ->
            _notes.value = notesList
        }
    }

    // メモを保存
    fun saveNote(title: String, content: String) {
        val noteId = UUID.randomUUID().toString()
        val note = Note(
            noteId = noteId,
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.saveNote(note)
        }
    }


}