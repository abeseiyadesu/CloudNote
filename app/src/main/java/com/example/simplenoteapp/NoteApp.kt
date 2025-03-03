package com.example.simplenoteapp

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.simplenoteapp.ui.home.HomeScreen
import com.example.simplenoteapp.ui.screen.EditScreen
import com.example.simplenoteapp.ui.screen.LoginScreen
import com.example.simplenoteapp.ui.screen.SignUpScreen

@Composable
fun NoteApp(startDestination: String = "login") {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        // NavController を生成
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = startDestination) {

            // ログイン画面へ
            composable("login"){
                LoginScreen(navController)
            }

            // アカウント作成画面へ
            composable("signup"){
                SignUpScreen(navController)
            }
            // ホーム画面へ移動
            composable(
                route = "home",
            ) {
                HomeScreen(
                    navController = navController
                )
            }

            //編集画面へ
            composable(
                // どのnoteを　特定　するため　Id　を渡す必要がある
                route = "edit/{noteId}",
                // Idは　Int型のため  Int  に変換する必要がある
                arguments = listOf(
                    androidx.navigation.navArgument("noteId") {
                        type = androidx.navigation.NavType.StringType
                    }
                ),
                // 左から右へスライドイン
                enterTransition = {
                    slideIntoContainer(
                        animationSpec = tween(100, easing = EaseIn),
                        // アニメーションが進行する方向を指定するプロパティ
                        // ➙
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                // 右から左へスライドアウト
                exitTransition = {
                    slideOutOfContainer(
                        animationSpec = tween(100, easing = EaseOut),
                        // アニメーションが進行する方向を指定するプロパティ
                        // ←
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                }
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")
                // nullチェック
                if (noteId != null) {
                    EditScreen(
                        navController = navController,
                        noteId = noteId
                    )
                }
            }
        }
    }
}