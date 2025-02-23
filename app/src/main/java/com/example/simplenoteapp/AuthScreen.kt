package com.example.simplenoteapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.simplenoteapp.auth.AuthViewModel

@Composable
fun AuthScreen(authViewModel: AuthViewModel, onLoginSuccess: () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(32.dp)) // 32dp のスペース

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (password.length < 6) {
//                    errorMessage = "パスワードは6文字以上でなければなりません。"
                } else {
                    if (isSignUp) {
                        authViewModel.signUp(email, password) { success ->
                            if (success) {
                                onLoginSuccess()
                            }
                        }
                    } else {
                        authViewModel.signIn(email, password) { success ->
                            if (success) {
                                onLoginSuccess()
                            }
                        }
                    }
                }
            }
        }) {
            if (isSignUp) {
                Text(text = "Sign Up")
            } else {
                Text(text = "Login")
            }

//            if (password.length < 6) {
//                Text(text = "パスワードは6文字以上でなければなりません。", color = Color.Red)
//            }

        }



        TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(if (isSignUp) "Already have an account? Login" else "New user? Sign up")
        }

    }

}