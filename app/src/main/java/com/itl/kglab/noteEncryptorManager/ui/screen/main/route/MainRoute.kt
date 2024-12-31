package com.itl.kglab.noteEncryptorManager.ui.screen.main.route

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.itl.kglab.noteEncryptorManager.manager.BiometricPromptManager
import com.itl.kglab.noteEncryptorManager.ui.data.BioAuthEvent
import com.itl.kglab.noteEncryptorManager.ui.screen.editor.EditorActivity
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.main.NoteListScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.main.SettingScreen
import com.itl.kglab.noteEncryptorManager.viewmodel.main.MainViewModel

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: MainViewModel,
    bioAuthManager: BiometricPromptManager
) {

    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

    val biometricResult by bioAuthManager.promptResult.collectAsState(initial = BiometricPromptManager.BioAuthResult.Init)
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
//            Log.d("TAG", "Activity result: $it")
        }
    )

    var bioAuthState by remember {
        mutableStateOf(BioAuthEvent())
    }

    // Result判斷，當biometricResult狀態改變即執行一次
    LaunchedEffect(biometricResult) {
        biometricResult.let { result ->
            when(result) {
                is BiometricPromptManager.BioAuthResult.AuthenticationError -> {
                    Log.d("TAG", "AuthenticationError: ${result.errorMessage}")
                }
                BiometricPromptManager.BioAuthResult.AuthenticationFailed -> {
                    Log.d("TAG", "AuthenticationFailed")
                }
                BiometricPromptManager.BioAuthResult.AuthenticationFailed -> {
                    Log.d("TAG", "AuthenticationFailed")
                }
                BiometricPromptManager.BioAuthResult.AuthenticationSuccess -> {
                    Log.d("TAG", "AuthenticationSuccess")
                    bioAuthState.func?.invoke() ?: run { }
                    bioAuthState = bioAuthState.copy(
                        func = null
                    )
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
                    val func = {
                        val id = info.id
                        val bundle = Bundle().apply {
                            putBoolean(EditorActivity.ARG_IS_EDIT, true)
                            putLong(EditorActivity.ARG_ID, id)
                        }

                        val intent = Intent(context, EditorActivity::class.java).apply {
                            putExtras(bundle)
                        }

                        context.startActivity(intent)
                    }

                    if (info.isPrivate) {
                        bioAuthState = bioAuthState.copy(
                            func = func
                        )

                        bioAuthManager.showBiometricPrompt(
                            title = "身份驗證",
                            desc = "請驗證身份編輯「${info.title}」"
                        )
                    } else {
                        func.invoke()
                    }

                },
                onItemDeleteClicked = { info ->
                    val func = {
                        viewModel.deleteNoteInfo(info)
                    }

                    if (info.isPrivate) {
                        bioAuthState = bioAuthState.copy(
                            func = { viewModel.deleteNoteInfo(info) }
                        )
                        bioAuthManager.showBiometricPrompt(
                            title = "身份驗證",
                            desc = "請驗證身份，隨後將刪除「${info.title}」"
                        )
                    } else {
                        func.invoke()
                    }
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