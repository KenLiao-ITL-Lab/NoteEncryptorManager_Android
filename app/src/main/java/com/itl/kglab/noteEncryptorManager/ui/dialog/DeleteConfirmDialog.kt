package com.itl.kglab.noteEncryptorManager.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

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
            Text(text = "確定要刪除？")
        },
        text = {
            Text(text = "您即將刪除「$noteTitle」，一旦刪除將無法復原。")
        },
        confirmButton = {
            TextButton(onClick = onConfirmClicked) {
                Text(text = "確認")
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = "取消")
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