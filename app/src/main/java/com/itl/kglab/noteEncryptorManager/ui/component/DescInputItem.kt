package com.itl.kglab.noteEncryptorManager.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DescInputItem(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    labelColor: Color = Color.Unspecified,
    supportingText: String = "",
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
    modifier = modifier,
    value = value,
    label = { Text(
        text = label,
        color = labelColor
    )},
    supportingText = if (supportingText.isBlank()) {
        null
    } else {
        {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(text = supportingText)
            }
        }
    },
    onValueChange = onValueChange,
    keyboardOptions = keyboardOptions
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewDescInputItem() {
    DescInputItem(
        value = "Preview Input",
        label = "Preview Label",
        supportingText = "Preview SupportingText",
        onValueChange = {}
    )
}