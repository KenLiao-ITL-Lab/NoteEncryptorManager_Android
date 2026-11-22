package com.itl.kglab.noteEncryptorManager.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.itl.kglab.noteEncryptorManager.R

@Composable
fun DeleteConfirmDialog(
    noteTitle: String = "",
    onConfirmClicked: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
        },
        title = {
            Text(text = stringResource(id = R.string.dialog_delete_confirm_title))
        },
        text = {
            Text(text = stringResource(id = R.string.dialog_delete_confirm_message, noteTitle))
        },
        confirmButton = {
            TextButton(onClick = onConfirmClicked) {
                Text(text = stringResource(id = R.string.common_confirm))
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(id = R.string.common_cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewDeleteConfirmDialog() {
    DeleteConfirmDialog(
        noteTitle = "測試筆記",
        onConfirmClicked = {},
        onDismissRequest = {}
    )
}
