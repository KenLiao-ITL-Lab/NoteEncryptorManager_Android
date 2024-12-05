package com.itl.kglab.noteEncryptorManager.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoteListScreen(
    modifier: Modifier = Modifier
        .fillMaxSize(),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NoteListItem(
            title = "測試標題", desc = "2024/12/05 16:36"
        )
    }
}


@Composable
fun NoteListItem(
    modifier: Modifier = Modifier,
    title: String,
    desc: String,
) {

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 4.dp),
                text = title,
                fontSize = 20.sp,
                maxLines = 1
            )

            NoteItemMenu(
                menuExpanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            )

            Icon(
                modifier = Modifier.size(width = 24.dp, height = 24.dp)
                    .clickable {
                        menuExpanded = true
                    },
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null
            )


        }

        Text(
            text = desc,
            color = Color.Gray
        )

    }
}


@Composable
fun NoteItemMenu(
    modifier: Modifier = Modifier,
    menuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onEditClicked: (() -> Unit)? = null,
    onDeleteClicked: (() -> Unit)? = null
) {

    Column(
        modifier = modifier
    ) {
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = onDismissRequest
        ) {

            DropdownMenuItem(
                text = { Text(text = "編輯") },
                onClick = {
                    onEditClicked?.invoke()
                }
            )

            DropdownMenuItem(
                text = { Text(text = "刪除") },
                onClick = {
                    onDeleteClicked?.invoke()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteListScreenPreview() {
    NoteListScreen()
}

@Preview(showBackground = true)
@Composable
fun NoteListItemPreview() {
    NoteListItem(
        title = "測試標題測試標題測試標題測試標題測試標題",
        desc = "2024/12/25 16:40"
    )
}

@Preview
@Composable
fun NoteItemMenuPreview() {
    NoteItemMenu(
        menuExpanded = true,
        onDismissRequest = {}
    )
}