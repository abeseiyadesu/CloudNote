package com.example.simplenoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.simplenoteapp.ui.theme.SimpleNoteAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Firestoreの初期化
        // Firestoreのログを確認
        FirebaseFirestore.setLoggingEnabled(true)

        val auth = FirebaseAuth.getInstance()

        // すでにログインしているユーザーが 再度ログインしなくていいようにする
        // ユーザーの認証状態を監視
        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser

            // currentUser の状態によってルートを決める
            val startDestination = if (currentUser != null) "home" else "login"
            setContent {
                SimpleNoteAppTheme {
                    // 初期画面を決定
                    NoteApp(startDestination = startDestination)
                }

            }
        }


    }
}
