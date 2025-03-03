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

    // Idに応じたノートを取得
    fun getNoteById(noteId: String, onComplete: (Note?) -> Unit) {
        repository.getNotes { notes ->
            val note = notes.find { it.noteId == noteId }
            onComplete(note)
        }
    }


    // メモを保存
    fun saveNote(noteId: String, title: String, content: String) {
        // noteIdが 0 の時 新規作成
        val finalNoteId = if (noteId == "0") {
            UUID.randomUUID().toString()
        } else {
            noteId
        }

        val note = Note(
            noteId = finalNoteId,
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        // メモを保存
        viewModelScope.launch {
            repository.saveNote(note)
        }
    }

    // メモを削除
    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNote(noteId)
        }
    }
}