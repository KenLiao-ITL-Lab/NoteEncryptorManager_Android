package com.itl.kglab.noteEncryptorManager.ui.screen.main.route

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.manager.BiometricPromptManager
import com.itl.kglab.noteEncryptorManager.ui.data.PendingAuthAction
import com.itl.kglab.noteEncryptorManager.ui.screen.detail.DetailActivity
import com.itl.kglab.noteEncryptorManager.ui.screen.editor.EditorActivity
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.main.NoteListScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.main.SettingScreen
import com.itl.kglab.noteEncryptorManager.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch

/**
 *  關於Type safety in Kotlin DSL and Navigation Compose 可參考下列文件
 *   - https://developer.android.com/guide/navigation/design/type-safety
 */

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: MainViewModel,
    bioAuthManager: BiometricPromptManager
) {

    val context = LocalContext.current

    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    // 記錄準備執行、但需要先經過生物辨識(指紋/臉部)的意圖動作 (Intent Action)
    // 辨識成功後，會在 LaunchedEffect 內根據這個狀態來決定要開啟哪一頁或執行哪個行為
    var pendingAuthAction by remember {
        mutableStateOf<PendingAuthAction?>(null)
    }

    val screenModifier = Modifier
//        .background(Color.White)
        .fillMaxSize()
        .padding(
            horizontal = dimensionResource(id = R.dimen.screen_table_padding)
        )

    // Result判斷，直接收集 (collect) 事件流，避免透過 State 可能造成的遺漏或重複觸發
    LaunchedEffect(bioAuthManager) {
        bioAuthManager.promptResult.collect { result ->
            when(result) {
                is BiometricPromptManager.BioAuthResult.AuthenticationError -> {
                    Log.d("TAG", "AuthenticationError: ${result.errorMessage}")
                }
                BiometricPromptManager.BioAuthResult.AuthenticationFailed -> {
                    Log.d("TAG", "AuthenticationFailed")
                }
                BiometricPromptManager.BioAuthResult.AuthenticationSuccess -> {
                    Log.d("TAG", "AuthenticationSuccess")
                    
                    when (val action = pendingAuthAction) {
                        is PendingAuthAction.OpenDetail -> {
                            val bundle = Bundle().apply {
                                putLong(DetailActivity.ARG_ID, action.id)
                            }
                            val intent = Intent(context, DetailActivity::class.java).apply {
                                putExtras(bundle)
                            }
                            context.startActivity(intent)
                        }
                        is PendingAuthAction.EditNote -> {
                            val bundle = Bundle().apply {
                                putBoolean(EditorActivity.ARG_IS_EDIT, true)
                                putLong(EditorActivity.ARG_ID, action.id)
                            }
                            val intent = Intent(context, EditorActivity::class.java).apply {
                                putExtras(bundle)
                            }
                            context.startActivity(intent)
                        }
                        is PendingAuthAction.DeleteNote -> {
                            viewModel.deleteNoteInfoById(action.id)
                        }
                        null -> {}
                    }
                    pendingAuthAction = null
                }
                BiometricPromptManager.BioAuthResult.FeatureUnavailable -> {
                    Log.d("TAG", "FeatureUnavailable")
                }
                BiometricPromptManager.BioAuthResult.HardwareUnavailable -> {
                    Log.d("TAG", "HardwareUnavailable")
                }
                BiometricPromptManager.BioAuthResult.AuthenticationNotSet -> {
                    if (Build.VERSION.SDK_INT >= 30) {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                            )
                        }
                        enrollLauncher.launch(enrollIntent)
                    }
                }

                BiometricPromptManager.BioAuthResult.Init -> {}
            }
        }
    }

    // 專門為 Compose Lifecycle 事件設計的 Effect，取代手動觀察 currentState
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.getNoteInfoList() // 更新資料
    }

    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = MainBottomNavigationItem.Converter
    ) {

        composable<MainBottomNavigationItem.Converter> {
            val keyboardManager = LocalSoftwareKeyboardController.current
            val clipboardManager = LocalClipboard.current
            val coroutineScope = rememberCoroutineScope()

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
                        val clipData = ClipData.newPlainText(
                            "plain text",
                            viewModel.resultState
                        )
                        coroutineScope.launch {
                            clipboardManager.setClipEntry(ClipEntry(clipData))
                        }
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
        composable<MainBottomNavigationItem.NoteList> {
            NoteListScreen(
                modifier = screenModifier,
                noteList = viewModel.state.noteInfoList,
                onItemCardClicked = { info ->
                    if (info.isPrivate) {
                        pendingAuthAction = PendingAuthAction.OpenDetail(info.id)

                        bioAuthManager.showBiometricPrompt(
                            title = "身份驗證",
                            desc = "請驗證身份查看「${info.title}」內容"
                        )
                    } else {
                        val bundle = Bundle().apply {
                            putLong(DetailActivity.ARG_ID, info.id)
                        }

                        val intent = Intent(context, DetailActivity::class.java).apply {
                            putExtras(bundle)
                        }

                        context.startActivity(intent)
                    }
                },
                onItemEditClicked = { info ->
                    if (info.isPrivate) {
                        pendingAuthAction = PendingAuthAction.EditNote(info.id)

                        bioAuthManager.showBiometricPrompt(
                            title = "身份驗證",
                            desc = "請驗證身份編輯「${info.title}」"
                        )
                    } else {
                        val bundle = Bundle().apply {
                            putBoolean(EditorActivity.ARG_IS_EDIT, true)
                            putLong(EditorActivity.ARG_ID, info.id)
                        }

                        val intent = Intent(context, EditorActivity::class.java).apply {
                            putExtras(bundle)
                        }

                        context.startActivity(intent)
                    }
                },
                onItemDeleteClicked = { info ->
                    if (info.isPrivate) {
                        pendingAuthAction = PendingAuthAction.DeleteNote(info.id)
                        
                        bioAuthManager.showBiometricPrompt(
                            title = "身份驗證",
                            desc = "請驗證身份，隨後將刪除「${info.title}」"
                        )
                    } else {
                        viewModel.deleteNoteInfoById(info.id)
                    }
                }
            )
        }
        composable<MainBottomNavigationItem.Setting> {
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