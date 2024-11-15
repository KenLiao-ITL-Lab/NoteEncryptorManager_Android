package com.itl.kglab.noteEncryptorManager.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton

@Composable
fun SaveNoteDialog(
    convertResult: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirmClick: (String) -> Unit = {}, // 回傳備註訊息
) {

    var noteInput by remember {
        mutableStateOf("")
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SaveNoteDialogTableGroup(
                    message = convertResult,
                    noteValue = noteInput,
                    onNoteInputChange = {
                        noteInput = it
                    }
                )
                Spacer(
                    modifier = Modifier
                        .padding(8.dp)
                )
                SaveNoteDialogButtonGroup(
                    onConfirmClick = {
                        onConfirmClick.invoke(noteInput)
                        onDismissRequest.invoke() // Dismiss
                    },
                    onCancelClick = {
                        onDismissRequest.invoke()
                    }
                )
            }
        }
    }
}

@Composable
fun SaveNoteDialogTableGroup(
    modifier: Modifier = Modifier,
    message: String = "",
    noteValue: String = "",
    onNoteInputChange: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {

        Text(
            modifier = Modifier
                .padding(8.dp),
            text = "訊息"
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = message,
            )
        }

        Spacer(
            modifier = Modifier
                .padding(8.dp)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = noteValue,
            label = {
                Text(text = "備註")
            },
            onValueChange = onNoteInputChange
        )

    }
}

@Composable
fun SaveNoteDialogButtonGroup(
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {

    val padding = 8.dp

    Row(
        modifier = modifier
    ) {
        OutlinedStyleButton(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = padding),
            buttonText = "儲存",
            onClick = onConfirmClick
        )

        OutlinedStyleButton(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = padding),
            buttonText = "取消",
            onClick = onCancelClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoteDialog() {
    SaveNoteDialog()
}

@Preview(showBackground = true)
@Composable
fun PreviewSaveNoteTable() {
    SaveNoteDialogTableGroup(
        message = "Preview Message"
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSaveNoteButtonGroup() {
    SaveNoteDialogButtonGroup()
}