package com.itl.kglab.noteEncryptorManager.ui.screen.editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.data.NoteEventData
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.editor.EditorViewData
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewData = viewModel.viewData.data
                    EditorScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewData = viewData,
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
            val data = EditorViewData(
                input = it.getString(ARG_INPUT) ?: "",
                output = it.getString(ARG_OUTPUT) ?: ""
            )
            viewModel.updateViewData(data)
        }
    }

    companion object {
        const val ARG_IS_EDIT = "IS_EDIT_BOOLEAN"
        const val ARG_ID = "ID_LONG"
        const val ARG_INPUT = "INPUT_STRING"
        const val ARG_OUTPUT = "OUTPUT_STRING"
        const val ARG_NOTE = "NOTE_STRING"
        const val ARG_IS_PRIVATE = "IS_PRIVATE_BOOLEAN"
    }
}


@Composable
fun EditorScreen(
    modifier: Modifier,
    viewData: EditorViewData,
    onSaveClicked: (NoteEventData) -> Unit,
    onCancelClicked: () -> Unit
) {

    var titleState by rememberSaveable {
        mutableStateOf(viewData.title)
    }

    var noteState by rememberSaveable {
        mutableStateOf(viewData.note)
    }

    var privateState by rememberSaveable {
        mutableStateOf(viewData.isPrivate)
    }

    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {

        ContextTable(
            inputText = viewData.input,
            outputText = viewData.output,
            titleText = titleState,
            onTitleTextChange = {
                titleState = it
            },
            noteText = noteState,
            onNoteTextChange = {
                noteState = it
            },
            isPrivate = privateState,
            onPrivateSwitchChange = {
                privateState = it
            }
        )

        EditorFunctionButtonGroup(
            modifier = Modifier.fillMaxWidth(),
            onSaveClicked = {
                val data = NoteEventData(
                    title = titleState,
                    inputMessage = viewData.input,
                    outputMessage = viewData.output,
                    note = noteState,
                    isPrivate = privateState
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

        val horizontalPadding = 8.dp

        // Title
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = horizontalPadding
                ),
            value = titleText,
            onValueChange = onTitleTextChange,
            label = {
                Text(text = "標題")
            }
        )

        // Note
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = horizontalPadding
                ),
            value = noteText,
            onValueChange = onNoteTextChange,
            label = {
                Text(text = "備註")
            }
        )

        // Input
        ContentTextCard(
            modifier = Modifier
                .padding(horizontal = horizontalPadding),
            label = "輸入",
            contentText = inputText
        )

        // Output
        ContentTextCard(
            modifier = Modifier
                .padding(horizontal = horizontalPadding),
            label = "輸出",
            contentText = outputText
        )


        // IsPrivate
        PrivateSwitch(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = horizontalPadding),
            isPrivate = isPrivate,
            onPrivateSwitchChange = onPrivateSwitchChange
        )

        HorizontalDivider(
            Modifier.padding(horizontalPadding)
        )

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
fun ContentTextCard(
    modifier: Modifier = Modifier,
    label: String,
    contentText: String
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                ),
            text = label
        )

        Text(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .fillMaxWidth()
                .padding(16.dp),
            text = contentText
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
        viewData = EditorViewData(),
        onSaveClicked = {},
        onCancelClicked = {}
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewContentTextCard() {
    ContentTextCard(
        modifier = Modifier
            .height(200.dp)
            .padding(vertical = 8.dp),
        label = "Content Label",
        contentText = "Content Text"
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