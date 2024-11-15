package com.itl.kglab.noteEncryptorManager.ui.component

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun OutlinedStyleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    buttonText: String = ""
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(text = buttonText)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOutlinedStyleButton() {
    OutlinedStyleButton(
        buttonText = "Preview Text"
    )
}