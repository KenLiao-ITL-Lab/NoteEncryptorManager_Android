package com.itl.kglab.noteEncryptorManager.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentTextCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    titleLabel: String = "",
    contentText: String = "",
    supportingText: String = "",
    onContentLongClicked: (() -> Unit)? = null
) {

    Column(
        modifier = modifier
            .background(backgroundColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val titleModifier = if (titleLabel.isNotEmpty()) {
                Modifier.padding(top = 12.dp)
            } else {
                Modifier
            }
            Text(
                modifier = titleModifier
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
            if (titleLabel.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                        .background(backgroundColor)
                        .padding(horizontal = 8.dp),
                    text = titleLabel,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (supportingText.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.End,
                text = supportingText,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewContentTextCard() {
    ContentTextCard(
        modifier = Modifier,
        titleLabel = "Content Label",
        contentText = "Content Text",
        supportingText = "Test supporting",
        onContentLongClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewContentTextCardWithEmptyTitle() {
    ContentTextCard(
        modifier = Modifier,
        titleLabel = "",
        contentText = "Content Text",
        supportingText = "",
        onContentLongClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewContentTextCardWithEmptySupporting() {
    ContentTextCard(
        modifier = Modifier,
        titleLabel = "Content Label",
        contentText = "Content Text",
        supportingText = "",
        onContentLongClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewContentTextCardWithLongContent() {
    ContentTextCard(
        modifier = Modifier,
        titleLabel = "Content Label is soooooooooooooooooooooooo AAAAAAAAAAAAAAAAAAAAAwesome",
        contentText = "Content Text, It will be a loooooooooooooooooooooon Meeeeeeeeeeeeeeesssssssssaaaaaaaggggggggeeeeee.",
        supportingText = "Test supporting is a looooooooooooooooong laaaaaaaaaaaaaaaabbbbbbbbbbeeeeeeeeelllllll",
        onContentLongClicked = {}
    )
}

