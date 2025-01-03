package com.itl.kglab.noteEncryptorManager.ui.screen.editor

import android.os.Bundle
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.ui.component.ContentTextCard
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.data.NoteEventData
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.editor.EditorViewModel
import com.itl.kglab.noteEncryptorManager.viewmodel.editor.EditorViewState
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
                    EditorScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = dimensionResource(id = R.dimen.screen_table_padding))
                        ,
                        viewState = viewModel.viewState,
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
    viewState: EditorViewState,
    onTableChanged: (NoteInfo) -> Unit,
    onSaveClicked: (NoteEventData) -> Unit,
    onCancelClicked: () -> Unit
) {

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {

        val noteInfo = viewState.noteInfo

        ContextTable(
            inputText = noteInfo.inputText,
            outputText = noteInfo.outputText,
            titleText = noteInfo.title,
            onTitleTextChange = {
                onTableChanged.invoke(noteInfo.copy(title = it))
            },
            noteText = noteInfo.note,
            onNoteTextChange = {
                onTableChanged.invoke(noteInfo.copy(note = it))
            },
            isPrivate = noteInfo.isPrivate,
            onPrivateSwitchChange = {
                onTableChanged.invoke(noteInfo.copy(isPrivate = it))
            }
        )

        EditorFunctionButtonGroup(
            modifier = Modifier.fillMaxWidth(),
            onSaveClicked = {
                val data = NoteEventData(
                    title = noteInfo.title,
                    inputMessage = noteInfo.inputText,
                    outputMessage = noteInfo.outputText,
                    note = noteInfo.note,
                    isPrivate = noteInfo.isPrivate
                )
                onSaveClicked(data)
            },
            onCancelClicked = onCancelClicked
        )
    }
}


@Composable
fun ContextTable(
    modifier: Modifier = Modifier,
    inputText: String,
    outputText: String,
    titleText: String,
    onTitleTextChange: (String) -> Unit,
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    isPrivate: Boolean,
    onPrivateSwitchChange: (Boolean) -> Unit
) {
    Column(
        modifier = modifier
    ) {

        // Title
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp
                ),
            value = titleText,
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
                    vertical = 16.dp,
                ),
            value = noteText,
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
            modifier = Modifier,
            label = "輸入",
            contentText = inputText
        )

        // Output
        ContentTextCard(
            modifier = Modifier,
            label = "輸出",
            contentText = outputText
        )


        // IsPrivate
        PrivateSwitch(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            isPrivate = isPrivate,
            onPrivateSwitchChange = onPrivateSwitchChange
        )

        HorizontalDivider()

    }
}

@Composable
fun PrivateSwitch(
    modifier: Modifier = Modifier,
    isPrivate: Boolean,
    onPrivateSwitchChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = "是否鎖定內容"
        )
        Switch(
            checked = isPrivate,
            onCheckedChange = onPrivateSwitchChange
        )
    }
}


@Composable
fun EditorFunctionButtonGroup(
    modifier: Modifier = Modifier,
    onSaveClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        OutlinedStyleButton(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            buttonText = "儲存",
            onClick = onSaveClicked
        )

        OutlinedStyleButton(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            buttonText = "取消",
            onClick = onCancelClicked
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditorScreen() {
    EditorScreen(
        modifier = Modifier
            .fillMaxSize(),
        viewState = EditorViewState(),
        onTableChanged = {},
        onSaveClicked = {},
        onCancelClicked = {}
    )
}




@Preview(showBackground = true)
@Composable
fun PreviewContextTable() {
    ContextTable(
        inputText = "輸入訊息",
        titleText = "標題",
        onTitleTextChange = {},
        outputText = "輸出訊息",
        noteText = "備註訊息",
        onNoteTextChange = {},
        isPrivate = true,
        onPrivateSwitchChange = {}
    )
}