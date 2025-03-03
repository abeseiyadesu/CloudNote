package com.example.simplenoteapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simplenoteapp.auth.AuthViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = AuthViewModel()
) {
    // ユーザー入力用
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // ユーザーのログイン状態を監視
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // ログイン成功時にホーム画面へ遷移
    if (isLoggedIn) {
        navController.navigate("home") {
            popUpTo("signup") {
                inclusive = true
            }
        }
    }
    // 全体カラム
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "新規登録")

        Spacer(modifier = Modifier.height(16.dp))
        // メールアドレス入力
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("メールアドレス") }
        )

        Spacer(modifier = Modifier.height(8.dp))
        // パスワード入力
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("パスワード") }
//            visualTransformation = PasswordVisualTransformation() // 入力したパスワードを***で隠す
        )

        Button(onClick = {
            // 入力されたアカウントを作成
            authViewModel.signUp(email, password)
        }) {
            Text(text = "アカウントを登録")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            navController.navigate("login")
        }) {
            Text(text = "ログイン画面へ")
        }

    }
}