package com.example.simplenoteapp.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.firebasenoteapp.R
import com.example.simplenoteapp.ui.data.Note
import com.example.simplenoteapp.ui.data.NoteViewModel

@Composable
fun EditScreen(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel(),
    noteId: String
) {
    var inputHeader by remember { mutableStateOf("") }
    var inputDetail by remember { mutableStateOf("") }

    // 初期値を設定
    LaunchedEffect(noteId) {
        if (noteId != "new") {
            noteViewModel.getNoteById(noteId) { note ->
                note?.let {
                    inputHeader = it.title
                    inputDetail = it.content
                }
            }
        }
    }

    // 戻るボタンの処理
    BackHandler {
        NavigateSaveBack(
            navController = navController,
            noteViewModel = noteViewModel,
            noteId = noteId,
            inputHeader = inputHeader,
            inputDetail = inputDetail,
            route = "home"
        )
    }

    // 全体
    Scaffold(
        // トップバー
        topBar = {
            EditScreenTopBar(
                navController = navController,
                noteId = noteId,
                inputHeader = inputHeader,
                inputDetail = inputDetail
            )
        },
        // メモ一覧表示
        content = { paddingValue ->
            EditScreenLayout(
                paddingValues = paddingValue,
                inputHeader = inputHeader,
                inputDetail = inputDetail,
                onHeaderChange = { inputHeader = it },
                onDetailChange = { inputDetail = it },

                )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreenTopBar(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel(),
    noteId: String,
    inputHeader: String,
    inputDetail: String
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    TopAppBar(title = {
        Text("")
    },
        // 色を指定
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorResource(id = R.color.top_bar_color),         // 背景色を指定
            titleContentColor = Color.White,            // タイトルの色を指定
            navigationIconContentColor = Color.White,   // ナビゲーションアイコンの色
            actionIconContentColor = Color.White        // アクションアイコンの色
        ),
        // 左上 戻るボタン
        navigationIcon = {
            IconButton(
                // 前の画面に戻る
                onClick = {
                    NavigateSaveBack(
                        navController = navController,
                        noteViewModel = noteViewModel,
                        noteId = noteId,
                        inputHeader = inputHeader,
                        inputDetail = inputDetail,
                        route = "home"
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back To Home"
                )
            }
        },
        actions = {
            IconButton(onClick = { showDeleteConfirmDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = Color.Red
                )
            }
        }
    )


    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("削除確認") },
            text = { Text("本当に削除しますか？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteViewModel.deleteNote(noteId) // 削除実行
                        showDeleteConfirmDialog = false
                        navController.navigate("home")
                    }
                ) {
                    Text("削除", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }
}

// データを保存する関数
// 複数使うため 関数化
fun NavigateSaveBack(
    navController: NavController,
    noteViewModel: NoteViewModel,
    noteId: String,
    inputHeader: String,
    inputDetail: String,
    route: String
) {
    // どちらも　入力が　空の場合　は　保存しないようにした
    if (inputHeader.trim().isNotBlank() || inputDetail.trim().isNotBlank()) {
        // データを保存する（新規作成または編集）
        noteViewModel.saveNote(
            noteId = noteId,
            title = inputHeader,
            content = inputDetail
        )
    }
    navController.navigate(route = route) // ホーム画面に戻る
}

@Composable
fun EditScreenLayout(
    paddingValues: PaddingValues,
    inputHeader: String,
    inputDetail: String,
    onHeaderChange: (String) -> Unit,
    onDetailChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        // 見出しの部分で改行ボタンを押したとき
        // 次のtextFieldへフォーカスさせたいため必要
        val focusRequester1 = remember { FocusRequester() }
        val focusRequester2 = remember { FocusRequester() }

        // 見出し
        TextField(
            value = inputHeader,
            onValueChange = { newHeader -> onHeaderChange(newHeader) },
            placeholder = { Text("タイトルを入力") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester1),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,       // 背景色（フォーカス外）
                focusedContainerColor = Color.White,         // 背景色（フォーカス時）
            ),
            textStyle = TextStyle(fontSize = 24.sp),         // 文字サイズを大きく
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            // 次の　TextFieldへ　フォーカス
            keyboardActions = KeyboardActions(
                onNext = { focusRequester2.requestFocus() }
            )

        )
        // メモ
        TextField(
            value = inputDetail,
            onValueChange = { newDetail -> onDetailChange(newDetail) },
            placeholder = { Text("詳細を入力") },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester2),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,      // 背景色（フォーカス外）
                focusedContainerColor = Color.White,        // 背景色（フォーカス時）
            ),
        )
    }
}