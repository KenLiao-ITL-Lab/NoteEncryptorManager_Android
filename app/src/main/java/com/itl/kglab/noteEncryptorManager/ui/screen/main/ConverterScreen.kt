package com.itl.kglab.noteEncryptorManager.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.data.NoteEventData
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreenIdRes.TAG_CONVERTER_BUTTON
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreenIdRes.TAG_FUNC_CLEAR_BUTTON
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreenIdRes.TAG_FUNC_COPY_BUTTON
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreenIdRes.TAG_FUNC_SAVE_BUTTON
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreenIdRes.TAG_INPUT_TEXT_FILED
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreenIdRes.TAG_RESULT_LABEL

object ConverterScreenIdRes {
    const val TAG_INPUT_TEXT_FILED = "input_text_filed" // 輸入
    const val TAG_CONVERTER_BUTTON = "converter_button" // 轉換
    const val TAG_RESULT_LABEL = "result_label" // 結果
    const val TAG_FUNC_COPY_BUTTON = "copy_button" // 複製
    const val TAG_FUNC_SAVE_BUTTON = "save_button" // 儲存
    const val TAG_FUNC_CLEAR_BUTTON = "clear_button" // 清除
}

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
                .padding(bottom = 8.dp)
                .testTag(TAG_INPUT_TEXT_FILED),
            value = inputValue,
            onValueChange = onInputChange,
            shape = RoundedCornerShape(12.dp),
            label = {
                Text(
                    text = stringResource(id = R.string.screen_converter_input_label)
                )
            }
        )

        OutlinedStyleButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .testTag(TAG_CONVERTER_BUTTON),
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
                .padding(16.dp)
                .testTag(TAG_RESULT_LABEL),
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
                .padding(horizontal = 16.dp)
                .testTag(TAG_FUNC_COPY_BUTTON),
            onClick = onDuplicateClicked,
            buttonText = stringResource(id = R.string.screen_converter_copy_button)
        )

        OutlinedStyleButton(
            modifier = Modifier
                .weight(buttonWeight)
                .padding(horizontal = 16.dp)
                .testTag(TAG_FUNC_SAVE_BUTTON),
            onClick = onSaveClicked,
            buttonText = stringResource(id = R.string.screen_converter_save_button)
        )

        OutlinedStyleButton(
            modifier = Modifier
                .weight(buttonWeight)
                .padding(horizontal = 16.dp)
                .testTag(TAG_FUNC_CLEAR_BUTTON),
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