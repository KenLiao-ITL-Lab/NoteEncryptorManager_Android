package com.itl.kglab.noteEncryptorManager.ui.screen.detail

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.manager.BiometricPromptManager
import com.itl.kglab.noteEncryptorManager.tools.SettingInputRegex
import com.itl.kglab.noteEncryptorManager.ui.component.ContentTextCard
import com.itl.kglab.noteEncryptorManager.ui.component.DescInputItem
import com.itl.kglab.noteEncryptorManager.ui.component.IconButtonEdit
import com.itl.kglab.noteEncryptorManager.ui.component.IconButtonReturn
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.component.TableDivider
import com.itl.kglab.noteEncryptorManager.ui.component.TitleFuncButtons
import com.itl.kglab.noteEncryptorManager.ui.dialog.LoadingDialog
import com.itl.kglab.noteEncryptorManager.ui.screen.editor.EditorActivity
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.detail.DetailViewModel
import com.itl.kglab.noteEncryptorManager.viewmodel.detail.NoteInfoTableData
import com.itl.kglab.noteEncryptorManager.viewmodel.detail.SampleSettingData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : ComponentActivity() {

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val noteId = intent.extras?.getLong(ARG_ID, -1L) ?: -1L

        setContent {
            val keyboardManager = LocalSoftwareKeyboardController.current
            val clipboardManager = LocalClipboard.current
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current

            var isSystemBiometricReady by remember {
                mutableStateOf(BiometricPromptManager.isSystemBiometricReady(context))
            }

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                isSystemBiometricReady = BiometricPromptManager.isSystemBiometricReady(context)
                viewModel.refreshBiometricSetting()
            }

            val isBiometricEffective = viewModel.isAppBiometricEnabled && isSystemBiometricReady

            LaunchedEffect(noteId) {
                if (noteId > 0L) {
                    viewModel.loadNoteInfoIfNeeded(noteId)
                }
            }

            val editorLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK && noteId > 0L) {
                        viewModel.refreshNoteInfo(noteId)
                    }
                }
            )

            fun copyToClipboard(content: String) {
                if (content.isBlank()) return
                val clipData = ClipData.newPlainText("plain text", content)
                coroutineScope.launch {
                    clipboardManager.setClipEntry(ClipEntry(clipData))
                    Toast.makeText(
                        context,
                        context.getString(R.string.common_copied_to_clipboard),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            NoteEncryptorManagerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val sampleTableEvent = SampleTableEvent(
                        onInputValueChange = { input ->
                            viewModel.setInput(input)
                        },
                        onConvertClicked = {
                            keyboardManager?.hide()
                            viewModel.convertMessage()
                        },
                        onOutputLongClicked = { output ->
                            copyToClipboard(output)
                        },
                        onSampleIndexValueChange = { index ->
                            if (index.matches(SettingInputRegex.sampleIndexInputRegex)) {
                                viewModel.setSampleIndex(index)
                            }
                        },
                        onSampleSizeValueChange = { size ->
                            if (size.matches(SettingInputRegex.sampleSizeInputRegex)) {
                                viewModel.setSampleSize(size)
                            }
                        },
                        onSampleClicked = {
                            keyboardManager?.hide()
                            viewModel.sampleMessage()
                        }
                    )
                    DetailScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        onBackClicked = {
                            finish()
                        },
                        onEditClicked = {
                            keyboardManager?.hide()
                            val bundle = Bundle().apply {
                                putLong(EditorActivity.ARG_ID, viewModel.noteInfoState.id)
                            }
                            val intent = Intent(context, EditorActivity::class.java).apply {
                                putExtras(bundle)
                            }
                            editorLauncher.launch(intent)
                        },
                        onNoteLongClicked = { content ->
                            copyToClipboard(content)
                        },
                        showLockIcon = viewModel.noteInfoState.isPrivate && isBiometricEffective,
                        sampleTableEvent = sampleTableEvent,
                        noteInfoTableData = viewModel.noteInfoState,
                        sampleSettingData = viewModel.settingState
                    )

                    if (viewModel.uiState.isLoading) {
                        LoadingDialog()
                    }
                }
            }
        }
    }

    companion object {
        const val ARG_ID = "ID_LONG"
    }
}

@Composable
fun DetailScreen(
    modifier: Modifier,
    onBackClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onNoteLongClicked: (String) -> Unit = {},
    showLockIcon: Boolean = false,
    sampleTableEvent: SampleTableEvent,
    noteInfoTableData: NoteInfoTableData = NoteInfoTableData(),
    sampleSettingData: SampleSettingData = SampleSettingData()
) {
    Column(
        modifier = modifier
    ) {
        TitleFuncButtons(
            leftButton = {
                IconButtonReturn { onBackClicked.invoke() }
            },
            rightButton = {
                IconButtonEdit { onEditClicked.invoke() }
            }
        )

        DetailTable(
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.screen_table_padding)
                )
                .verticalScroll(rememberScrollState()),
            onNoteLongClicked = onNoteLongClicked,
            showLockIcon = showLockIcon,
            sampleTableEvent = sampleTableEvent,
            noteInfoTableData = noteInfoTableData,
            sampleSettingData = sampleSettingData
        )
    }
}

@Composable
fun DetailTable(
    modifier: Modifier = Modifier,
    onNoteLongClicked: (String) -> Unit,
    showLockIcon: Boolean = false,
    sampleTableEvent: SampleTableEvent,
    noteInfoTableData: NoteInfoTableData = NoteInfoTableData(),
    sampleSettingData: SampleSettingData = SampleSettingData()
) {
    Column(
        modifier = modifier
    ) {
        NoteInfoDetailTable(
            modifier = Modifier
                .fillMaxWidth(),
            noteInfoTableData = noteInfoTableData,
            showLockIcon = showLockIcon,
            onContentLongClicked = onNoteLongClicked,
        )

        SimpleTable(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp),
            sampleSettingData = sampleSettingData,
            sampleEvent = sampleTableEvent
        )
    }
}

@Composable
fun InformationTitleCard(
    title: String,
    timeDesc: String,
    isPrivate: Boolean
) {
    Row {
        if (isPrivate) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Is Private"
            )
        }

        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f),
            text = title,
            fontSize = 20.sp,
        )
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp),
        textAlign = TextAlign.End,
        text = timeDesc,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun NoteInfoDetailTable(
    modifier: Modifier,
    noteInfoTableData: NoteInfoTableData,
    showLockIcon: Boolean = noteInfoTableData.isPrivate,
    onContentLongClicked: (String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                InformationTitleCard(
                    title = noteInfoTableData.title,
                    timeDesc = noteInfoTableData.time,
                    isPrivate = showLockIcon
                )

                TableDivider(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    desc = ""
                )

                ContentTextCard(
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    contentText = noteInfoTableData.note,
                    onContentLongClicked = {
                        onContentLongClicked.invoke(
                            noteInfoTableData.note
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SimpleTable(
    modifier: Modifier = Modifier,
    sampleSettingData: SampleSettingData,
    sampleEvent: SampleTableEvent
) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            TableDivider(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 24.dp),
                desc = stringResource(id = R.string.screen_detail_converter_section),
            )

            DescInputItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                value = sampleSettingData.input,
                label = stringResource(id = R.string.screen_detail_input),
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onValueChange = sampleEvent.onInputValueChange,
                supportingText = stringResource(id = R.string.screen_detail_input_supporting),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedStyleButton(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                buttonText = stringResource(id = R.string.screen_converter_convert_button),
                onClick = sampleEvent.onConvertClicked
            )

            ContentTextCard(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                contentText = sampleSettingData.output,
                supportingText = stringResource(id = R.string.common_long_press_to_copy),
                onContentLongClicked = {
                    sampleEvent.onOutputLongClicked(sampleSettingData.output)
                }
            )

            TableDivider(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                desc = stringResource(id = R.string.screen_detail_sample_section)
            )

            SampleSettingInput(
                settingData = sampleSettingData,
                sampleTableEvent = sampleEvent
            )

            OutlinedStyleButton(
                modifier = Modifier
                    .padding(
                        horizontal = 8.dp,
                        vertical = 16.dp
                    )
                    .fillMaxWidth(),
                buttonText = stringResource(id = R.string.screen_detail_sample_button),
                onClick = sampleEvent.onSampleClicked
            )

            ContentTextCard(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                contentText = sampleSettingData.sampledMessage,
                supportingText = stringResource(id = R.string.common_long_press_to_copy),
                onContentLongClicked = {
                    sampleEvent.onOutputLongClicked(sampleSettingData.sampledMessage)
                }
            )
        }
    }
}

@Composable
private fun SampleSettingInput(
    settingData: SampleSettingData,
    sampleTableEvent: SampleTableEvent
) {
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {
        DescInputItem(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
            label = stringResource(id = R.string.screen_detail_sample_index_label),
            supportingText = stringResource(id = R.string.screen_detail_sample_index_supporting),
            value = settingData.sampleIndex,
            onValueChange = { indexString ->
                sampleTableEvent.onSampleIndexValueChange.invoke(indexString)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            )
        )

        DescInputItem(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            label = stringResource(id = R.string.screen_detail_sample_size_label),
            supportingText = stringResource(id = R.string.screen_detail_sample_size_supporting),
            value = settingData.sampleSize,
            onValueChange = { sizeString ->
                sampleTableEvent.onSampleSizeValueChange.invoke(sizeString)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailScreen() {
    DetailScreen(
        modifier = Modifier,
        onBackClicked = {},
        onEditClicked = {},
        sampleTableEvent = SampleTableEvent(
            onInputValueChange = {},
            onConvertClicked = {},
            onOutputLongClicked = {},
            onSampleIndexValueChange = {},
            onSampleSizeValueChange = {},
            onSampleClicked = {}
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewInformationScreen() {
    DetailTable(
        onNoteLongClicked = {},
        sampleTableEvent = SampleTableEvent(
            onInputValueChange = {},
            onConvertClicked = {},
            onOutputLongClicked = {},
            onSampleIndexValueChange = {},
            onSampleSizeValueChange = {},
            onSampleClicked = {}
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNoteInfoDetailTable() {
    NoteInfoDetailTable(
        modifier = Modifier,
        noteInfoTableData = NoteInfoTableData(
            title = "This is Title container",
            time = "2024-08-11 16:33",
            note = "I wanna tell you something.",
            isPrivate = true
        ),
        onContentLongClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleTable() {
    SimpleTable(
        sampleSettingData = SampleSettingData(),
        sampleEvent = SampleTableEvent(
            onInputValueChange = {},
            onConvertClicked = {},
            onOutputLongClicked = {},
            onSampleIndexValueChange = {},
            onSampleSizeValueChange = {},
            onSampleClicked = {}
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSampleSettingInput() {
    SampleSettingInput(
        settingData = SampleSettingData(),
        sampleTableEvent = SampleTableEvent(
            onInputValueChange = {},
            onConvertClicked = {},
            onOutputLongClicked = {},
            onSampleIndexValueChange = {},
            onSampleSizeValueChange = {},
            onSampleClicked = {}
        )
    )
}

data class SampleTableEvent(
    val onConvertClicked: () -> Unit,
    val onInputValueChange: (String) -> Unit,
    val onOutputLongClicked: (String) -> Unit,
    val onSampleIndexValueChange: (String) -> Unit,
    val onSampleSizeValueChange: (String) -> Unit,
    val onSampleClicked: () -> Unit
)
