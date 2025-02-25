package com.example.simplenoteapp.ui.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

// 読み書き操作をまとめるRepository
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
//
//    // メモを読み込む
//    suspend fun getNotes(onResult:(List<Note>)->Unit): List<Note> {
//        val userId = getUserId() ?: return emptyList()
//        val snapshot = db.collection("notes")
//            .document(userId)
//            .collection("userNotes")
//            .get()
//            .await()
//        return snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
//    }

    // メモをリアルタイム読み込み
    fun getNotes(onResult: (List<Note>) -> Unit) {
        // getUserId が Nullの場合 関数を終了 する
        val userId = getUserId() ?: return

        db.collection("notes")
            .document(userId)
            .collection("userNotes")
            .orderBy("timestamp", Query.Direction.DESCENDING)// 新しい順に取得
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Firestore", "メモの取得に失敗", exception)
                    return@addSnapshotListener
                }
                val notes=snapshot?.documents?.mapNotNull { it.toObject(Note::class.java) }?: emptyList()
                onResult(notes)
            }
    }
}