package com.example.simplenoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import com.example.simplenoteapp.ui.theme.SimpleNoteAppTheme
import com.google.firebase.firestore.FirebaseFirestore

data class Memo(val title: String, val content: String){
    constructor() : this("", "") // Firestore 用の引数なしコンストラクタ
}

val db = FirebaseFirestore.getInstance()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        addMemo()
        setContent {
            SimpleNoteAppTheme {
                NoteApp()
            }

        }
    }
}

fun addMemo() {
    val memo = Memo("新しいメモだよ", "これは irestore に保存されたメモですね")

    db.collection("memos") // "memos" というコレクション（フォルダ）を作る
        .add(memo) // メモを追加
        .addOnSuccessListener { documentReference ->
            println("メモを追加しました！ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("エラー: $e")
        }
}