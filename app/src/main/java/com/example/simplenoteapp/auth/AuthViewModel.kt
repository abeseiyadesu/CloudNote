package com.example.simplenoteapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    // Firestoreに接続するためインスタンスを取得
    // これを使って データの読み書き をするらしい
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // ログイン状態を管理する
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    // エラーメッセージを管理する
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage

    // 新規登録処理
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _isLoggedIn.value = true
                    } else {
                        _errorMessage.value = "登録に失敗しました"
                    }
                }
        }
    }

    // ログイン処理
    fun login(email: String, password: String) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _isLoggedIn.value = true
                    } else {
                        _errorMessage.value = "ログインに失敗しました"
                    }
                }
        }
    }

    // ログアウト
    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
    }

}