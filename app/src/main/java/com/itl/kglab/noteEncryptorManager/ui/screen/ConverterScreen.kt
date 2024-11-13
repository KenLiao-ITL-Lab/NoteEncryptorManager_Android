package com.itl.kglab.noteEncryptorManager.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    resultText: String,
    onConvertClicked: (String) -> Unit,
    onDuplicateClicked: () -> Unit,
    onSaveClicked: () -> Unit,
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
            onSaveClicked = onSaveClicked,
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
                .padding(bottom = 8.dp),
            value = inputValue,
            onValueChange = onInputChange,
            label = {
                Text("輸入")
            }
        )

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            onClick = onConvertClicked,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
            )
        ) {
            Text(text = "轉換")
        }
    }
}

@Composable
fun ConverterResultSection(
    resultText: String
) {
    OutlinedCard(
        modifier = Modifier
            .height(200.dp)
            .padding(vertical = 8.dp),
        border = BorderStroke(1.dp, Color.Black)
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

        OutlinedButton(
            modifier = Modifier
                .weight(buttonWeight)
                .padding(horizontal = 16.dp),
            onClick = onDuplicateClicked,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
            )
        ) {
            Text(
                text = "複製"
            )
        }

        OutlinedButton(
            modifier = Modifier
                .weight(buttonWeight)
                .padding(horizontal = 16.dp),
            onClick = onSaveClicked,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
            )
        ) {
            Text(text = "儲存")
        }

        OutlinedButton(
            modifier = Modifier
                .weight(buttonWeight)
                .padding(horizontal = 16.dp),
            onClick = onClearClicked,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
            )
        ) {
            Text(text = "清除")
        }
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