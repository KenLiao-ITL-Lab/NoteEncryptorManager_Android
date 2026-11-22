package com.itl.kglab.noteEncryptorManager.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentTextCard(
    modifier: Modifier = Modifier,
    contentText: String = "",
    supportingText: String = "",
    onContentLongClicked: (() -> Unit)? = null
) {
    val cardShape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
    ) {
        val baseCardModifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .clip(cardShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = cardShape
            )
        val interactiveModifier = if (onContentLongClicked != null) {
            baseCardModifier.combinedClickable(
                onClick = {},
                onLongClick = onContentLongClicked
            )
        } else {
            baseCardModifier
        }

        Box(
            modifier = interactiveModifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = contentText
            )
        }
        if (supportingText.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.End,
                text = supportingText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        contentText = "Content Text, It will be a loooooooooooooooooooooon Meeeeeeeeeeeeeeesssssssssaaaaaaaggggggggeeeeee.",
        supportingText = "Test supporting is a looooooooooooooooong laaaaaaaaaaaaaaaabbbbbbbbbbeeeeeeeeelllllll",
        onContentLongClicked = {}
    )
}
