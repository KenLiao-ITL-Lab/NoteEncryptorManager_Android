package com.itl.kglab.noteEncryptorManager.ui.screen.main


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.data.NoteEventData

@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    resultText: String,
    onConvertClicked: (String) -> Unit,
    onDuplicateClicked: () -> Unit,
    onSaveClicked: (NoteEventData) -> Unit,
    onClearClicked: () -> Unit
) {
    var inputValue by rememberSaveable { mutableStateOf("") }

//    var showSaveDialog by remember {
//        mutableStateOf(false)
//    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ConverterInputSection(
            inputValue = inputValue,
            onInputChange = { input ->
                inputValue = input
            },
            onConvertClicked = {
                onConvertClicked.invoke(inputValue)
            }
        )
        ConverterResultSection(
            resultText = resultText
        )
        ConverterFunctionalButtonGroup(
            onDuplicateClicked = onDuplicateClicked,
            onSaveClicked = {
                // 不是空白才儲存
                if (resultText.isNotBlank()) {
                    val saveInfo = NoteEventData(
                        inputMessage = inputValue,
                        outputMessage = resultText
                    )
                    onSaveClicked.invoke(saveInfo)
                }
            },
            onClearClicked = {
                onClearClicked.invoke()
                inputValue = ""
            }
        )
    }

//    if (showSaveDialog) {
//        SaveNoteDialog(
//            onDismissRequest = {
//                showSaveDialog = false
//            },
//            onConfirmClick = { note ->
//                // 彙整SaveNote
//                onSaveClicked.invoke(
//                    SaveNoteEventData(
//                        inputMessage = inputValue,
//                        result = resultText,
//                        note = note
//                    )
//                )
//            },
//            convertResult = resultText
//        )
//    }
}

@Composable
fun ConverterInputSection(
    inputValue: String,
    onInputChange: (String) -> Unit,
    onConvertClicked: () -> Unit
) {

    Column {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 8.dp),
            value = inputValue,
            onValueChange = onInputChange,
            label = {
                Text(
                    text = stringResource(id = R.string.screen_converter_input_label)
                )
            }
        )

        OutlinedStyleButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            onClick = onConvertClicked,
            buttonText = stringResource(id = R.string.screen_converter_convert_button)
        )

    }
}

@Composable
fun ConverterResultSection(
    resultText: String
) {
    OutlinedCard(
        modifier = Modifier
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            text = resultText
        )
    }
}

@Composable
fun ConverterFunctionalButtonGroup(
    onDuplicateClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onClearClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        val buttonWeight = 1f

        OutlinedStyleButton(
            modifier = Modifier
                .weight(buttonWeight)
                .padding(horizontal = 16.dp),
            onClick = onDuplicateClicked,
            buttonText = stringResource(id = R.string.screen_converter_copy_button)
        )

        OutlinedStyleButton(
            modifier = Modifier
                .weight(buttonWeight)
                .padding(horizontal = 16.dp),
            onClick = onSaveClicked,
            buttonText = stringResource(id = R.string.screen_converter_save_button)
        )

        OutlinedStyleButton(
            modifier = Modifier
                .weight(buttonWeight)
                .padding(horizontal = 16.dp),
            onClick = onClearClicked,
            buttonText = stringResource(id = R.string.screen_converter_clear_button)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InputSectionPreview() {
    ConverterInputSection(
        inputValue = "Input String",
        onInputChange = {},
        onConvertClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewResultSection() {
    ConverterResultSection(
        resultText = "Result Text"
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFunctionButtonGroup() {
    ConverterFunctionalButtonGroup(
        onDuplicateClicked = {},
        onSaveClicked = {},
        onClearClicked = {}
    )
}