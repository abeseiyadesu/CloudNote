package com.example.simplenoteapp.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AuthViewModel : ViewModel() {
    // Firebase認証インスタンスを取得
    private val auth = FirebaseAuth.getInstance()

    private val _currentUserId= MutableLiveData<String?>()
    val currentUserId:LiveData<String?> get() = _currentUserId

    init{
        auth.addAuthStateListener { firebaseAuth->
            _currentUserId.value=firebaseAuth.currentUser?.uid
        }
    }

    // メール・パスワード で 新規登録
    fun signUp(email: String, password: String, onResult: (Boolean) -> Unit) {
        // 新規ユーザー作成
        auth.createUserWithEmailAndPassword(email, password)
            // 登録処理が終了した時
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
                if (!task.isSuccessful) {
                    Log.e("Auth", "登録失敗", task.exception)
                }else{
                    Log.e("Auth", "登録成功", task.exception)
                }
            }
    }

    // ログイン
    fun signIn(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
                if (!task.isSuccessful) {
                    Log.e("Auth", "ログイン失敗", task.exception)
                }else{
                    if (!task.isSuccessful) {
                        Log.e("Auth", "ログイン失敗", task.exception)
                    }
                }
            }
    }

    // ログアウト
    fun signOut(){
        auth.signOut()
    }

    // 現在のユーザーIdを取得
    fun getCurrentUserId():String?{
        return auth.currentUser?.uid
    }
}