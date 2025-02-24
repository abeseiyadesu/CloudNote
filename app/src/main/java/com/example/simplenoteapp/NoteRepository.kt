package com.example.simplenoteapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class NoteRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 現在のユーザーIDを取得
    private fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    // メモを保存
    suspend fun saveNote(note: Note) {
        val userId = getUserId() ?: return
        val noteRef = db.collection("notes")
            .document(userId)
            .collection("userNotes")
            .document(note.noteId)

        noteRef.set(note).await()// Firestoreに保存
    }

    // メモを読み込む
    suspend fun getNotes(): List<Note> {
        val userId = getUserId() ?: return emptyList()
        val snapshot = db.collection("notes")
            .document(userId)
            .collection("userNotes")
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
    }


}