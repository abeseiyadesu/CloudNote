package com.example.simplenoteapp.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.colorResource
import com.example.firebasenoteapp.R
import com.example.simplenoteapp.auth.AuthViewModel
import com.example.simplenoteapp.ui.data.Note
import com.example.simplenoteapp.ui.data.NoteViewModel
import kotlinx.coroutines.launch

private enum class OpenedSwipeAbleState {
    INITIAL,
    OPENED,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel()
) {
    val notes by noteViewModel.notes.collectAsState()

    // Scaffold構造でアプリのレイアウトを構築
    Scaffold(
        topBar = {
            // トップバー
            HomeScreenTopBar(
                title = "",
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.top_bar_color),         // 背景色を指定
                    titleContentColor = Color.White,                // タイトルの色を指定
                    navigationIconContentColor = Color.White,       // ナビゲーションアイコンの色
                    actionIconContentColor = Color.White            // アクションアイコンの色
                ),
                navController = navController
            )
        },
        floatingActionButton = {
            HomeScreenFloatingActionButton(
                navController = navController
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) { }
            // メインコンテンツ
            HomeScreenLayout(
                notes = notes,
                paddingValues = paddingValues,
                navController = navController
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    title: String,
    colors: TopAppBarColors,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    // ダイアログ表示用
    var openDialog = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(title)
        },
        // top barの　色　を指定する
        colors = colors,
        // ログアウトボタン
        actions = {
            IconButton(onClick = {
                openDialog.value = true
            }) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "ログアウト"
                )
            }

        }

    )

    // 関数化したほうがいい
    // ログアウト確認ダイアログ
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false }, // ダイアログ外のタップで閉じる
            title = { Text("ログアウト") },
            text = { Text("ログアウトしますか？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("home_screen") { inclusive = true } // home画面を飛ばす
                        }
                    }
                ) {
                    Text("ログアウト")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text("キャンセル")
                }
            }
        )
    }

}

@Composable
fun HomeScreenFloatingActionButton(
    navController: NavController
) {
    FloatingActionButton(
        onClick = {
            // 画面遷移　EditScreenへ
            navController.navigate("edit/0")
        },
        // 形を丸に設定
        shape = CircleShape,
        containerColor = colorResource(id = R.color.top_bar_color),
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        contentColor = Color.White
    ) {
        // アイコン設定
        Icon(
            Icons.Filled.Add,
            contentDescription = ""
        )
    }
}

@Composable
fun HomeScreenLayout(
    notes: List<Note>,
    paddingValues: PaddingValues,
    navController: NavController,
    noteViewModel: NoteViewModel = viewModel()
) {
    var currentNoteToDelete by remember { mutableStateOf<Note?>(null) } // 削除するノートを一時的に保持

    Column(
        modifier = Modifier
            .padding(paddingValues)
    ) {
        // ノート数 表示
        HorizontalDivider(thickness = 1.dp)
        Text(
            text = "${notes.size}個のノート",
            fontSize = 10.sp,
            modifier = Modifier.padding(4.dp)
        )
        // 横線
        HorizontalDivider(thickness = 1.dp)

        // メモ一覧
        LazyColumn(
            modifier = Modifier.padding(
            ),
        ) {
            items(notes) { note ->
                HorizontalDivider(thickness = 1.dp)
                SwipeableRow(
                    noteId = note.noteId,
                    onSwipe = {
                        currentNoteToDelete = note
                    }
                ) {
                    NoteItems(
                        onTap = {
                            navController.navigate("edit/${note.noteId}")
                        },
                        header = note.title
                    )
                }
                HorizontalDivider(thickness = 1.dp)
            }
        }

        //　削除時ダイアログを出す
        currentNoteToDelete?.let { note ->
            AlertDialog(
                onDismissRequest = { currentNoteToDelete = null }, //
                title = { Text(text = "削除の確認") },
                text = { Text(text = "本当に削除しますか？この操作は元に戻せません。") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // 削除ボタンが押されたらnoteを消す
                            noteViewModel.deleteNote(note.noteId)

                            // 現在のnoteを消すと アラートは必要なくなるので NULL にする
                            // null にしない場合 アラートが出続ける
                            currentNoteToDelete = null
                        }
                    ) {
                        Text(text = "削除")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { currentNoteToDelete = null }
                    ) {
                        Text(text = "キャンセル")
                    }
                }
            )
        }
    }
}


@Composable
fun NoteItems(
    onTap: () -> Unit,
    header: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    // 長押しなど拡張可能
                    // シングルタップ
                    onTap = { onTap() }
                )
            }
            .height(100.dp)
    ) {
        Text(
            text = header,
            modifier = Modifier.padding(8.dp)
        )
    }
}

// スワイプ削除関数
@SuppressLint("RememberReturnType")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeableRow(
    noteId: String,
    onSwipe: () -> Unit,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints {
        val constraintsScope = this
        // 削除ボタンの横幅
        val deleteButtonWidth = 64.dp
        val deleteButtonWidthPx = with(LocalDensity.current) {
            deleteButtonWidth.toPx()
        }
        val anchors = DraggableAnchors {
            OpenedSwipeAbleState.INITIAL at 0f
            OpenedSwipeAbleState.OPENED at deleteButtonWidthPx
        }
        val decayAnimationSpec = rememberSplineBasedDecay<Float>()
        val anchorDraggableState = remember(noteId) {
            AnchoredDraggableState(
                initialValue = OpenedSwipeAbleState.INITIAL,
                confirmValueChange = {
                    when (it) {
                        OpenedSwipeAbleState.INITIAL -> {
                            // do nothing
                        }

                        OpenedSwipeAbleState.OPENED -> {
                            // Opened Event
                        }
                    }
                    true
                },
                anchors = anchors,
                positionalThreshold = { distance: Float -> distance * 0.5f },
                velocityThreshold = { 5000f }, // 横スワイプですぐに消えてしまうため、大きい数値を設定
                snapAnimationSpec = SpringSpec(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                decayAnimationSpec = decayAnimationSpec,
            )
        }

        Box(
            Modifier.anchoredDraggable(
                state = anchorDraggableState,
                reverseDirection = true,
                orientation = Orientation.Horizontal,
            )
        ) {
            if (anchorDraggableState.currentValue != OpenedSwipeAbleState.INITIAL) {
                DeleteButtonLayout(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    onSwipe = {
                        onSwipe()
                        coroutineScope.launch {
                            anchorDraggableState.snapTo(OpenedSwipeAbleState.INITIAL)
                        }
                    }

                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .offset { IntOffset(-anchorDraggableState.offset.roundToInt(), 0) }
            ) {
                content()
            }
        }
    }
}

@Composable
fun DeleteButtonLayout(
    modifier: Modifier = Modifier,
    onSwipe: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(64.dp)
            .background(Color.Red)
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    // 長押しなど拡張可能
                    // シングルタップ
                    onTap = { onSwipe() }
                )
            }
            .height(100.dp)
            .zIndex(0f),  // 背面に配置するために zIndex を 0 に設定
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "削除",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreenTopBarPreview() {
    HomeScreenTopBar(
        title = "",
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorResource(id = R.color.top_bar_color),         // 背景色を指定
            titleContentColor = Color.White,                // タイトルの色を指定
            navigationIconContentColor = Color.White,       // ナビゲーションアイコンの色
            actionIconContentColor = Color.White            // アクションアイコンの色
        ),
        navController = rememberNavController()
    )
}

@Preview
@Composable
fun HomeScreenLayoutPreview() {
    val navController = rememberNavController()
    val paddingValues = PaddingValues(16.dp)
    // 仮のデータを用意
//    val notes = listOf(
//        Note(id = 1, header = "Comic 1", detail = ""),
//        Note(id = 2, header = "Comic 2", detail = ""),
//        Note(id = 3, header = "Comic 3", detail = "")
//    )
//
//    HomeScreenLayout(
//        notes = notes,
//        paddingValues = paddingValues,
//        navController = navController
//    )
}
