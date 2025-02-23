package com.example.simplenoteapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.simplenoteapp.auth.AuthViewModel
import com.example.simplenoteapp.data.freespace.FreeSpace
import com.example.simplenoteapp.data.freespace.FreeSpaceRepository
import com.example.simplenoteapp.data.note.Note
import com.example.simplenoteapp.data.note.NoteRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.switchMap


class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteRepository: NoteRepository


    // 現在のユーザーIDを保持するLiveData
    private val _currentUserId = MutableLiveData<String>()

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes") //コレクションを作る
    private val authViewModel = AuthViewModel()

    // フリースペース用リポジトリ
    private val freeSpaceRepository: FreeSpaceRepository

    val freeSpace: LiveData<FreeSpace?>

    init {
        val noteDatabase = AppDatabase.getDatabase(application)

        noteRepository = NoteRepository(noteDatabase.noteDao())

        freeSpaceRepository = FreeSpaceRepository(noteDatabase.freeSpaceDao())
        freeSpace = freeSpaceRepository.freeSpace

        // 初回起動時にデータがnullの場合データをセットする
        viewModelScope.launch {
            freeSpaceRepository.initializeFreeSpace()
        }

        _currentUserId.value = authViewModel.getCurrentUserId()
    }

    // 現在のユーザーノートを獲得する
    var allNotes: LiveData<List<Note>> = _currentUserId.switchMap { uid ->
        noteRepository.getNotesForUser(uid)
    }

    fun switchUser(userId: String) {
        _currentUserId.value = userId
    }

    fun insertNote(note: Note) = viewModelScope.launch {
        noteRepository.insert(note)
        saveNoteToFirestore(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.update(note)
        saveNoteToFirestore(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteRepository.delete(note)
        deleteNoteFromFirestore(note.id)
    }


    fun getNoteById(id: Int): LiveData<Note?> {
        val noteLiveData = MutableLiveData<Note?>()
        viewModelScope.launch {
            val note = noteRepository.getNoteById(id)
            noteLiveData.postValue(note)
        }
        return noteLiveData
    }

    // フリースペースの更新
    fun updateFreeSpace(header: String, detail: String) = viewModelScope.launch {
        freeSpaceRepository.updateFreeSpace(header, detail)
    }

    // firestore
    fun saveNoteToFirestore(note: Note) {
        // ユーザーIDを取得
        val userId = authViewModel.getCurrentUserId() ?: return
        // そのユーザーの メモ用コレクションを 指定
        val userNotesCollection = db.collection("users").document(userId).collection("notes")
        // 非同期処理で firestoreに データを保存
        viewModelScope.launch {
            try {
                userNotesCollection.document(note.id.toString()).set(note).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Firestoreからデータを取得 Roomへ保存
    fun fetchNotesFromFirestore() = viewModelScope.launch {
        // ユーザーIDを取得
        val userId = authViewModel.getCurrentUserId() ?: return@launch
        // 全メモを取得
        val userNotesCollection = db.collection("users").document(userId).collection("notes")

        // ネットワーク切断時の可能性を考えtryを使う
        try {
            val snapshot = userNotesCollection
                .get()
                .await()
            val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
            // FireStoreのデータをRoomに保存
            notes.forEach { note ->
                val existingNote = noteRepository.getNoteById(note.id)
                if (existingNote == null) {
                    // FIrebaseにないノートを保存
                    noteRepository.insert(note)
                } else {
                    //　既存のノートを上書き
                    noteRepository.update(note)
                }
            }
        } catch (e: Exception) {
            // 発生した 例外を 標準エラーに 出力
            e.printStackTrace()
        }
    }

    // Firestoreのデータを削除
    private suspend fun deleteNoteFromFirestore(id: Int) {
        val userId = _currentUserId.value ?: return
        val userNotesCollection = notesCollection.document(userId).collection("notes")
        try {
            userNotesCollection.document(id.toString()).delete().await()
        } catch (e: Exception) {
            // 発生した 例外を 標準エラーに 出力
            e.printStackTrace()
        }
    }

    // Firestoreのリアルタイムリスナー（Firestoreのデータが変わると自動更新）
    fun listenForFirestoreUpdates() {
        val userId = _currentUserId.value ?: return
        val userNoteCollection = notesCollection.document(userId).collection("notes")
        userNoteCollection.addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                viewModelScope.launch {
                    val notes = it.documents.mapNotNull { doc -> doc.toObject(Note::class.java) }
                    notes.forEach { noteRepository.insert(it) }
                }
            }

        }
    }
}