package com.itl.kglab.noteEncryptorManager.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentTextCard(
    modifier: Modifier = Modifier,
    label: String,
    contentText: String,
    onContentLongClicked: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
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
                .padding(16.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        onContentLongClicked?.invoke()
                    }
                ),
            text = contentText
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewContentTextCard() {
    ContentTextCard(
        modifier = Modifier,
        label = "Content Label",
        contentText = "Content Text",
        onContentLongClicked = {}
    )
}
