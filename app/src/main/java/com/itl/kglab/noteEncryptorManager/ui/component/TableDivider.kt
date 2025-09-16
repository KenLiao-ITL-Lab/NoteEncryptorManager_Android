package com.itl.kglab.noteEncryptorManager.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TableDivider(
    modifier: Modifier,
    desc: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        )

        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            textAlign = TextAlign.End,
            fontSize = 10.sp,
            color = Color.Gray,
            text = desc
        )
    }
}