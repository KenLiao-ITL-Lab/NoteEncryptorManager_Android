package com.itl.kglab.noteEncryptorManager.ui.screen.editor

import android.content.ClipData
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.ui.component.ContentTextCard
import com.itl.kglab.noteEncryptorManager.ui.component.IconButtonReturn
import com.itl.kglab.noteEncryptorManager.ui.component.IconButtonSave
import com.itl.kglab.noteEncryptorManager.ui.component.TitleFuncButtons
import com.itl.kglab.noteEncryptorManager.ui.data.NoteEventData
import com.itl.kglab.noteEncryptorManager.ui.dialog.LoadingDialog
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.editor.EditorNoteInfoData
import com.itl.kglab.noteEncryptorManager.viewmodel.editor.EditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditorActivity : ComponentActivity() {

    private val viewModel: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initViewData()

        setContent {
            val clipboardManager = LocalClipboard.current
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.saveCompleted.collect {
                    setResult(RESULT_OK)
                    finish()
                }
            }

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
                    val noteInfo = viewModel.viewState.noteInfo
                    EditorScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = dimensionResource(id = R.dimen.screen_table_padding)),
                        noteInfo = noteInfo,
                        onTableChanged = {
                            viewModel.updateNoteInfo(
                                title = it.title,
                                note = it.note,
                                isPrivate = it.isPrivate
                            )
                        },
                        onSaveClicked = {
                            viewModel.saveNoteInfo()
                        },
                        onCancelClicked = {
                            finish()
                        },
                        onContentLongClicked = { content ->
                            copyToClipboard(content)
                        }
                    )

                    if (viewModel.isSaving) {
                        LoadingDialog()
                    }
                }
            }
        }
    }

    private fun initViewData() {
        val bundle = intent.extras
        bundle?.let {
            val id = bundle.getLong(ARG_ID, ARG_DEFAULT_ID)

            if (id == ARG_DEFAULT_ID) {
                viewModel.setNewInfoIfNeeded(
                    input = it.getString(ARG_INPUT) ?: "",
                    output = it.getString(ARG_OUTPUT) ?: ""
                )
            } else {
                viewModel.findNoteInfoIfNeeded(id)
            }
        }
    }

    companion object {
        const val ARG_DEFAULT_ID = -1L
        const val ARG_IS_EDIT = "IS_EDIT_BOOLEAN"
        const val ARG_ID = "ID_LONG"
        const val ARG_INPUT = "INPUT_STRING"
        const val ARG_OUTPUT = "OUTPUT_STRING"
    }
}

@Composable
fun EditorScreen(
    modifier: Modifier,
    noteInfo: EditorNoteInfoData,
    onTableChanged: (EditorNoteInfoData) -> Unit,
    onSaveClicked: (NoteEventData) -> Unit,
    onCancelClicked: () -> Unit,
    onContentLongClicked: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        TitleFuncButtons(
            leftButton = {
                IconButtonReturn {
                    onCancelClicked.invoke()
                }
            },
            rightButton = {
                IconButtonSave {
                    val data = NoteEventData(
                        title = noteInfo.title,
                        inputMessage = noteInfo.input,
                        outputMessage = noteInfo.output,
                        note = noteInfo.note,
                        isPrivate = noteInfo.isPrivate
                    )
                    onSaveClicked(data)
                }
            }
        )

        ContextTable(
            modifier = Modifier.padding(horizontal = 8.dp),
            noteInfo = noteInfo,
            onTitleTextChange = {
                onTableChanged.invoke(noteInfo.copy(title = it))
            },
            onNoteTextChange = {
                onTableChanged.invoke(noteInfo.copy(note = it))
            },
            onPrivateSwitchChange = {
                onTableChanged.invoke(noteInfo.copy(isPrivate = it))
            },
            onContentLongClicked = onContentLongClicked
        )
    }
}

@Composable
fun ContextTable(
    modifier: Modifier = Modifier,
    noteInfo: EditorNoteInfoData,
    onTitleTextChange: (String) -> Unit,
    onNoteTextChange: (String) -> Unit,
    onPrivateSwitchChange: (Boolean) -> Unit,
    onContentLongClicked: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 10.dp,
                    bottom = 0.dp,
                    start = 8.dp,
                    end = 8.dp
                ),
            textAlign = TextAlign.End,
            text = noteInfo.time
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = noteInfo.title,
            maxLines = 1,
            onValueChange = onTitleTextChange,
            label = {
                Text(text = stringResource(id = R.string.screen_editor_title_label))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                modifier = Modifier,
                checked = noteInfo.isPrivate,
                onCheckedChange = onPrivateSwitchChange,
            )

            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.screen_editor_private_checkbox_label)
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = noteInfo.note,
            onValueChange = onNoteTextChange,
            label = {
                Text(text = stringResource(id = R.string.screen_editor_note_label))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            )
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
        )

        ContentTextCard(
            modifier = Modifier
                .padding(vertical = 8.dp),
            supportingText = stringResource(id = R.string.common_long_press_to_copy),
            contentText = noteInfo.input,
            onContentLongClicked = {
                onContentLongClicked(noteInfo.input)
            }
        )

        ContentTextCard(
            modifier = Modifier
                .padding(vertical = 8.dp),
            supportingText = stringResource(id = R.string.common_long_press_to_copy),
            contentText = noteInfo.output,
            onContentLongClicked = {
                onContentLongClicked(noteInfo.output)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditorScreen() {
    EditorScreen(
        modifier = Modifier
            .fillMaxSize(),
        noteInfo = EditorNoteInfoData(),
        onTableChanged = {},
        onSaveClicked = {},
        onCancelClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewContextTable() {
    val noteInfo = EditorNoteInfoData(
        time = "2025/11/01",
        title = "標題",
        note = "訊息備註",
        input = "輸日訊息",
        output = "輸出訊息",
        isPrivate = true
    )

    ContextTable(
        noteInfo = noteInfo,
        onTitleTextChange = {},
        onNoteTextChange = {},
        onPrivateSwitchChange = {}
    )
}
