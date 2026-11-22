package com.itl.kglab.noteEncryptorManager.ui.screen.main.route

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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

    var showEnrollDialog by rememberSaveable { mutableStateOf(false) }
    var showSystemBiometricRemovedDialog by rememberSaveable { mutableStateOf(false) }
    var disableBiometricPrivateCount by rememberSaveable { mutableStateOf<Int?>(null) }

    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    val editorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.clear()
            }
        }
    )

    fun openSystemBiometricSettings() {
        val enrollIntent = if (Build.VERSION.SDK_INT >= 30) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
            }
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
        enrollLauncher.launch(enrollIntent)
    }

    fun checkSystemBiometricConsistency() {
        val isReady = BiometricPromptManager.isSystemBiometricReady(context)
        viewModel.updateSystemBiometricReady(isReady)
        if (viewModel.state.isSettingLoaded &&
            viewModel.state.settingInfo.isBiometricEnabled &&
            !isReady
        ) {
            viewModel.disableBiometricBecauseSystemRemoved()
            showSystemBiometricRemovedDialog = true
        }
    }

    val screenModifier = Modifier
        .fillMaxSize()
        .padding(
            horizontal = dimensionResource(id = R.dimen.screen_table_padding)
        )

    // 首次設定載入完成或設定變動時檢查系統身份辨識一致性（需求 5）
    LaunchedEffect(
        viewModel.state.isSettingLoaded,
        viewModel.state.settingInfo.isBiometricEnabled
    ) {
        checkSystemBiometricConsistency()
    }

    // 直接收集 (collect) 生物辨識事件流，並由 ViewModel 保存 pendingAuthAction 以跨組態變更
    LaunchedEffect(bioAuthManager) {
        bioAuthManager.promptResult.collect { result ->
            when (result) {
                is BiometricPromptManager.BioAuthResult.AuthenticationError -> {
                    viewModel.clearPendingAuthAction()
                    if (result.errorMessage.isNotBlank()) {
                        Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                BiometricPromptManager.BioAuthResult.AuthenticationFailed -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.biometric_auth_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                BiometricPromptManager.BioAuthResult.AuthenticationSuccess -> {
                    when (val action = viewModel.pendingAuthAction) {
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
                        is PendingAuthAction.ToggleBiometric -> {
                            val currentSetting = viewModel.state.settingInfo
                            val newSetting = currentSetting.copy(
                                isBiometricEnabled = action.targetState,
                                info = currentSetting.info.copy(isBiometricEnabled = action.targetState)
                            )
                            viewModel.saveSettingInfo(newSetting)
                        }
                        null -> {}
                    }
                    viewModel.clearPendingAuthAction()
                }
                BiometricPromptManager.BioAuthResult.FeatureUnavailable,
                BiometricPromptManager.BioAuthResult.HardwareUnavailable -> {
                    viewModel.clearPendingAuthAction()
                    Toast.makeText(
                        context,
                        context.getString(R.string.biometric_unavailable),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                BiometricPromptManager.BioAuthResult.AuthenticationNotSet -> {
                    viewModel.clearPendingAuthAction()
                    showEnrollDialog = true
                }
                BiometricPromptManager.BioAuthResult.Init -> {}
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.getNoteInfoList()
        checkSystemBiometricConsistency()
    }

    // 系統尚未設定生物辨識引導對話框
    if (showEnrollDialog) {
        AlertDialog(
            onDismissRequest = { showEnrollDialog = false },
            title = { Text(stringResource(id = R.string.biometric_enroll_dialog_title)) },
            text = { Text(stringResource(id = R.string.biometric_enroll_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEnrollDialog = false
                        openSystemBiometricSettings()
                    }
                ) {
                    Text(stringResource(id = R.string.biometric_enroll_dialog_go_settings))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEnrollDialog = false }
                ) {
                    Text(stringResource(id = R.string.common_cancel))
                }
            }
        )
    }

    // 需求 5：系統取消身份辨識後的自動關閉提醒對話框
    if (showSystemBiometricRemovedDialog) {
        AlertDialog(
            onDismissRequest = { showSystemBiometricRemovedDialog = false },
            title = { Text(stringResource(id = R.string.dialog_system_biometric_removed_title)) },
            text = { Text(stringResource(id = R.string.dialog_system_biometric_removed_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSystemBiometricRemovedDialog = false
                        openSystemBiometricSettings()
                    }
                ) {
                    Text(stringResource(id = R.string.biometric_enroll_dialog_go_settings))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSystemBiometricRemovedDialog = false }
                ) {
                    Text(stringResource(id = R.string.common_confirm))
                }
            }
        )
    }

    // 需求 2：關閉 App 身份辨識時，若資料庫已有上鎖筆記則跳出確認對話框
    disableBiometricPrivateCount?.let { privateCount ->
        AlertDialog(
            onDismissRequest = { disableBiometricPrivateCount = null },
            title = { Text(stringResource(id = R.string.dialog_disable_biometric_title)) },
            text = {
                Text(
                    stringResource(
                        id = R.string.dialog_disable_biometric_message,
                        privateCount
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        disableBiometricPrivateCount = null
                        viewModel.updatePendingAuthAction(PendingAuthAction.ToggleBiometric(false))
                        bioAuthManager.showBiometricPrompt(
                            title = context.getString(R.string.biometric_auth_title),
                            desc = context.getString(R.string.biometric_prompt_disable)
                        )
                    }
                ) {
                    Text(stringResource(id = R.string.common_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { disableBiometricPrivateCount = null }
                ) {
                    Text(stringResource(id = R.string.common_cancel))
                }
            }
        )
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
                inputText = viewModel.inputState,
                onInputChange = {
                    viewModel.updateInput(it)
                },
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
                            Toast.makeText(
                                context,
                                context.getString(R.string.common_copied_to_clipboard),
                                Toast.LENGTH_SHORT
                            ).show()
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
                    editorLauncher.launch(intent)
                },
                onClearClicked = {
                    viewModel.clear()
                }
            )
        }
        composable<MainBottomNavigationItem.NoteList> {
            val isBiometricEffective = viewModel.state.isBiometricEffective

            NoteListScreen(
                modifier = screenModifier,
                noteList = viewModel.state.noteInfoList,
                isBiometricEffective = isBiometricEffective,
                onBiometricHintClicked = {
                    navHostController.navigate(MainBottomNavigationItem.Setting) {
                        popUpTo(navHostController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                onItemCardClicked = { info ->
                    if (info.isPrivate && isBiometricEffective) {
                        viewModel.updatePendingAuthAction(PendingAuthAction.OpenDetail(info.id))
                        bioAuthManager.showBiometricPrompt(
                            title = context.getString(R.string.biometric_auth_title),
                            desc = context.getString(R.string.biometric_prompt_view_note, info.title)
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
                    if (info.isPrivate && isBiometricEffective) {
                        viewModel.updatePendingAuthAction(PendingAuthAction.EditNote(info.id))
                        bioAuthManager.showBiometricPrompt(
                            title = context.getString(R.string.biometric_auth_title),
                            desc = context.getString(R.string.biometric_prompt_edit_note, info.title)
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
                    if (info.isPrivate && isBiometricEffective) {
                        viewModel.updatePendingAuthAction(PendingAuthAction.DeleteNote(info.id))
                        bioAuthManager.showBiometricPrompt(
                            title = context.getString(R.string.biometric_auth_title),
                            desc = context.getString(R.string.biometric_prompt_delete_note, info.title)
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
                },
                onToggleBiometric = { targetState ->
                    val supportState = bioAuthManager.checkBiometricSupport()
                    if (supportState == BiometricPromptManager.BioAuthResult.HardwareUnavailable ||
                        supportState == BiometricPromptManager.BioAuthResult.FeatureUnavailable
                    ) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.biometric_not_supported),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (targetState && supportState == BiometricPromptManager.BioAuthResult.AuthenticationNotSet) {
                        showEnrollDialog = true
                    } else if (!targetState) {
                        // 需求 2：關閉前檢查資料庫是否有上鎖筆記
                        viewModel.checkPrivateNoteCount { privateCount ->
                            if (privateCount > 0) {
                                disableBiometricPrivateCount = privateCount
                            } else {
                                viewModel.updatePendingAuthAction(PendingAuthAction.ToggleBiometric(false))
                                bioAuthManager.showBiometricPrompt(
                                    title = context.getString(R.string.biometric_auth_title),
                                    desc = context.getString(R.string.biometric_prompt_disable)
                                )
                            }
                        }
                    } else {
                        viewModel.updatePendingAuthAction(PendingAuthAction.ToggleBiometric(true))
                        bioAuthManager.showBiometricPrompt(
                            title = context.getString(R.string.biometric_auth_title),
                            desc = context.getString(R.string.biometric_prompt_enable)
                        )
                    }
                }
            )
        }
    }
}
