//package com.example.simplenoteapp.ui.screen
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.simplenoteapp.data.NoteViewModel
//import com.example.simplenoteapp.data.freespace.FreeSpace
//import com.example.simplenoteapp.data.note.Note
//
//@Composable
//fun FreeSpaceScreen(
//    navController: NavController,
//    viewModel: NoteViewModel = viewModel()
//){
//    val freeSpace by viewModel.freeSpace.observeAsState()
//
//    var inputHeader by remember { mutableStateOf(freeSpace?.header ?: "") }
//    var inputDetail by remember { mutableStateOf(freeSpace?.detail ?: "") }
//
//    LaunchedEffect(freeSpace) {
//        freeSpace?.let {
//            inputHeader = it.header
//            inputDetail = it.detail
//        }
//    }
//
//
//    Scaffold(
//        topBar = {
//            FreeSpaceScreenTopBar(
//                navController = navController,
//                inputHeader = inputHeader,
//                inputDetail = inputDetail
//            )
//        },
//        content = { paddingValue ->
//            FreeSpaceScreenLayout(
//                paddingValues = paddingValue,
//                navController = navController,
//                inputHeader = inputHeader,
//                inputDetail = inputDetail,
//                onHeaderChange = { inputHeader = it },
//                onDetailChange = { inputDetail = it }
//            )
//        }
//    )
//}
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FreeSpaceScreenTopBar(
//    navController: NavController,
//    noteViewModel: NoteViewModel = viewModel(),
//    inputHeader: String,
//    inputDetail: String
//) {
//    TopAppBar(
//        title = {
//            Text("")
//        },
//        // topbar 色を指定する
//        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//            containerColor = Color(0xFF25beb1),         // 背景色を指定
//            titleContentColor = Color.White,            // タイトルの色を指定
//            navigationIconContentColor = Color.White,   // ナビゲーションアイコンの色
//            actionIconContentColor = Color.White        // アクションアイコンの色
//        ),
//        // 左上 戻るボタン
//        navigationIcon = {
//            IconButton(
//                // 前の画面に戻る
//                onClick = {
//                    noteViewModel.updateFreeSpace(inputHeader,inputDetail)
//                    // 戻れる画面があるかを確認
//                    if (navController.previousBackStackEntry != null) {
//                        navController.popBackStack()
//                    } else {
//                        navController.navigate("home") // ホーム画面に戻る
//                    }
//                }
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = "Back To Home"
//                )
//            }
//        }
//    )
//}
//
//
//@Composable
//fun FreeSpaceScreenLayout(
//    paddingValues: PaddingValues,
//    navController: NavController,
//    inputHeader: String,
//    inputDetail: String,
//    onHeaderChange: (String) -> Unit,
//    onDetailChange: (String) -> Unit
//) {
//    Column(
//        modifier = Modifier.padding(paddingValues)
//    ) {
//        TextField(
//            value = inputHeader,
//            onValueChange = { newHeader -> onHeaderChange(newHeader) },
//            label = { Text("") },
//            modifier = Modifier
//                .fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        TextField(
//            value = inputDetail,
//            onValueChange = { newDetail -> onDetailChange(newDetail) },
//            label = { Text("") },
//            modifier = Modifier
//                .fillMaxSize()
//        )
//    }
//}