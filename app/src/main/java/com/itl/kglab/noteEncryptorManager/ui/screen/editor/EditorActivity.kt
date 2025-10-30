package com.itl.kglab.noteEncryptorManager.ui.screen.editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.ui.component.ContentTextCard
import com.itl.kglab.noteEncryptorManager.ui.component.IconButtonReturn
import com.itl.kglab.noteEncryptorManager.ui.component.IconButtonSave
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.component.TitleFuncButtons
import com.itl.kglab.noteEncryptorManager.ui.data.NoteEventData
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.editor.EditorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditorActivity : ComponentActivity() {

    private val viewModel: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initViewData()

        setContent {
            NoteEncryptorManagerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val noteInfo = viewModel.viewState.noteInfo
                    EditorScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = dimensionResource(id = R.dimen.screen_table_padding))
                        ,
                        noteInfo = noteInfo,
                        onTableChanged = {
                            viewModel.updateNoteInfo(it)
                        },
                        onSaveClicked = {
                            viewModel.saveNoteInfo(it)
                            finish()
                        },
                        onCancelClicked = {
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun initViewData() {
        val bundle = intent.extras
        bundle?.let {
            val isEditor = it.getBoolean(ARG_IS_EDIT)
            if (isEditor) {
                val id = bundle.getLong(ARG_ID)
                viewModel.findNoteInfo(id = id)
            } else {
                val noteInfo = NoteInfo(
                    id = 0,
                    inputText = it.getString(ARG_INPUT) ?: "",
                    outputText = it.getString(ARG_OUTPUT) ?: ""
                )
                viewModel.updateNoteInfo(noteInfo)
            }
        }
    }

    companion object {
        const val ARG_IS_EDIT = "IS_EDIT_BOOLEAN"
        const val ARG_ID = "ID_LONG"
        const val ARG_INPUT = "INPUT_STRING"
        const val ARG_OUTPUT = "OUTPUT_STRING"
    }
}


@Composable
fun EditorScreen(
    modifier: Modifier,
    noteInfo: NoteInfo,
    onTableChanged: (NoteInfo) -> Unit,
    onSaveClicked: (NoteEventData) -> Unit,
    onCancelClicked: () -> Unit
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
                        inputMessage = noteInfo.inputText,
                        outputMessage = noteInfo.outputText,
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
            }
        )

    }
}


@Composable
fun ContextTable(
    modifier: Modifier = Modifier,
    noteInfo: NoteInfo,
    onTitleTextChange: (String) -> Unit,
    onNoteTextChange: (String) -> Unit,
    onPrivateSwitchChange: (Boolean) -> Unit
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
            text = noteInfo.timeDesc
        )

        // Title
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp
                ),
            value = noteInfo.title,
            maxLines = 1,
            onValueChange = onTitleTextChange,
            label = {
                Text(text = "標題")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        // Note
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp,
                ),
            value = noteInfo.note,
            onValueChange = onNoteTextChange,
            label = {
                Text(text = "備註")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            )
        )

        // Input
        ContentTextCard(
            modifier = Modifier
                .height(200.dp)
                .padding(vertical = 8.dp),
            titleLabel = "輸入",
            supportingText = "長按可複製",
            contentText = noteInfo.inputText
        )

        // Output
        ContentTextCard(
            modifier = Modifier
                .height(200.dp)
                .padding(vertical = 8.dp),
            titleLabel = "輸出",
            supportingText = "長按可複製",
            contentText = noteInfo.outputText
        )

        HorizontalDivider()

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditorScreen() {
    val noteInfo = NoteInfo(
        timeDesc = "2025-11-01"
    )
    EditorScreen(
        modifier = Modifier
            .fillMaxSize(),
        noteInfo = noteInfo,
        onTableChanged = {},
        onSaveClicked = {},
        onCancelClicked = {}
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewContextTable() {

    val noteInfo = NoteInfo(
        timeDesc = "2025/11/01",
        title = "標題",
        note = "訊息備註",
        inputText = "輸日訊息",
        outputText = "輸出訊息",
        isPrivate = true
    )

    ContextTable(
        noteInfo = noteInfo,
        onTitleTextChange = {},
        onNoteTextChange = {},
        onPrivateSwitchChange = {}
    )
}