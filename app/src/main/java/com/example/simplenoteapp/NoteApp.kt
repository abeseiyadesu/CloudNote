package com.example.simplenoteapp

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.simplenoteapp.auth.AuthViewModel
import com.example.simplenoteapp.ui.home.HomeScreen
import com.example.simplenoteapp.ui.screen.EditScreen
import com.example.simplenoteapp.ui.screen.FreeSpaceScreen

@Composable
fun NoteApp() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        // NavController を生成
        val navController = rememberNavController()

        // AuthViewModelをViewModelScope内で管理する
        val authViewModel: AuthViewModel = viewModel()

        NavHost(navController = navController, startDestination = "auth") {
            composable(
                route = "auth"
            ) {
                AuthScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }

            // ホーム画面へ移動
            composable(
                route = "home",
            ) {
                HomeScreen(
                    navController = navController
                )
            }

            //
            composable(
                // どのnoteを　特定　するため　Id　を渡す必要がある
                route = "edit/{noteId}",
                // Idは　Int型のため  Int  に変換する必要がある
                arguments = listOf(
                    androidx.navigation.navArgument("noteId") {
                        type = androidx.navigation.NavType.IntType
                    }
                ),
                // 移動する方に書く
                enterTransition = {
                    slideIntoContainer(
                        animationSpec = tween(100, easing = EaseIn),
                        // アニメーションが進行する方向を指定するプロパティ
                        // ➙
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },  // 左から右へスライドイン
                exitTransition = {
                    slideOutOfContainer(
                        animationSpec = tween(100, easing = EaseOut),
                        // アニメーションが進行する方向を指定するプロパティ
                        // ←
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                }   // 右から左へスライドアウト
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getInt("noteId")
                // nullチェック
                if (noteId != null) {
                    EditScreen(
                        navController = navController,
                        noteId = noteId
                    )
                }
            }

            // フリースペースへ遷移
            composable(
                route = "freespace",

                // 移動する方に書く
                enterTransition = {
                    slideIntoContainer(
                        animationSpec = tween(100, easing = EaseIn),
                        // アニメーションが進行する方向を指定するプロパティ
                        // ➙
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },  // 左から右へスライドイン
                exitTransition = {
                    slideOutOfContainer(
                        animationSpec = tween(100, easing = EaseOut),
                        // アニメーションが進行する方向を指定するプロパティ
                        // ←
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                }   // 右から左へスライドアウト
            ) {
                FreeSpaceScreen(
                    navController = navController,
                )
            }

        }
    }
}