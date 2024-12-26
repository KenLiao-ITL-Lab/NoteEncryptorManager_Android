package com.itl.kglab.noteEncryptorManager.ui.screen.main.route

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.itl.kglab.noteEncryptorManager.ui.screen.editor.EditorActivity
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.main.NoteListScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.main.SettingScreen
import com.itl.kglab.noteEncryptorManager.viewmodel.main.MainViewModel

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: MainViewModel
) {

    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState) {
        when(lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.getNoteInfoList() // 更新資料
            }
            else -> {}
        }
    }

    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = MainBottomNavigationItem.Converter.route
    ) {

        val screenModifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 16.dp
            )

        composable(MainBottomNavigationItem.Converter.route) {
            val keyboardManager = LocalSoftwareKeyboardController.current
            val clipboardManager = LocalClipboardManager.current

            ConverterScreen(
                modifier = screenModifier,
                resultText = viewModel.resultState,
                onConvertClicked = { input ->
                    keyboardManager?.hide()
                    viewModel.convertInput(input)
                },
                onDuplicateClicked = {
                    keyboardManager?.hide()
                    if (viewModel.resultState.isNotBlank()) {
                        clipboardManager.setText(AnnotatedString(viewModel.resultState))
                    }
                },
                onSaveClicked = { data ->
                    keyboardManager?.hide()
                    val bundle = Bundle().apply {
                        putBoolean(EditorActivity.ARG_IS_EDIT, false)
                        putString(EditorActivity.ARG_OUTPUT, data.outputMessage)
                        putString(EditorActivity.ARG_INPUT, data.inputMessage)
                    }
                    val intent = Intent(context, EditorActivity::class.java).apply {
                        putExtras(bundle)
                    }
                    context.startActivity(intent)
                },
                onClearClicked = {
                    viewModel.clear()
                }
            )
        }
        composable(MainBottomNavigationItem.NoteList.route) {
            NoteListScreen(
                modifier = screenModifier,
                noteList = viewModel.state.noteInfoList,
                onItemEditClicked = { info ->
                    val id = info.id
                    val bundle = Bundle().apply {
                        putBoolean(EditorActivity.ARG_IS_EDIT, true)
                        putLong(EditorActivity.ARG_ID, id)
                    }

                    val intent = Intent(context, EditorActivity::class.java).apply {
                        putExtras(bundle)
                    }

                    context.startActivity(intent)
                },
                onItemDeleteClicked = { info ->
                    viewModel.deleteNoteInfo(info)
                }
            )
        }
        composable(MainBottomNavigationItem.Setting.route) {
            SettingScreen(
                modifier = screenModifier,
                hashTypeList = viewModel.getHashTypeList(),
                settingInfo = viewModel.state.settingInfo,
                onSaveSettingClicked = { info ->
                    viewModel.saveSettingInfo(info)
                }
            )
        }
    }
}