package com.itl.kglab.noteEncryptorManager.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.ui.data.DeleteNoteDataEvent
import com.itl.kglab.noteEncryptorManager.ui.dialog.DeleteConfirmDialog

@Composable
fun NoteListScreen(
    modifier: Modifier = Modifier,
    noteList: List<NoteInfo>,
    onItemCardClicked: (NoteInfo) -> Unit,
    onItemEditClicked: (NoteInfo) -> Unit,
    onItemDeleteClicked: (NoteInfo) -> Unit
) {

    var deleteEventState by remember {
        mutableStateOf(DeleteNoteDataEvent())
    }

    if (noteList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.screen_list_empty_list_desc),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(noteList) { noteInfo ->
                NoteListItem(
                    info = noteInfo,
                    onItemCardClicked = {
                        onItemCardClicked.invoke(noteInfo)
                    },
                    onItemEditClicked = {
                        onItemEditClicked.invoke(noteInfo)
                    },
                    onItemDeleteClicked = {
                        // 將當下的NoteInfo帶進DeleteEventState
                        deleteEventState = deleteEventState.copy(
                            noteInfo = noteInfo,
                            isShowConfirmDialog = true
                        )
                    }
                )
            }
        }
    }

    if (deleteEventState.isShowConfirmDialog) {
        deleteEventState.noteInfo?.let { info ->
            DeleteConfirmDialog(
                noteTitle = info.title,
                onConfirmClicked = {
                    onItemDeleteClicked(info)
                    // 還原DeleteEventState
                    deleteEventState = deleteEventState.copy(
                        noteInfo = null,
                        isShowConfirmDialog = false
                    )
                },
                onDismissRequest = {
                    // 還原DeleteEventState
                    deleteEventState = deleteEventState.copy(
                        noteInfo = null,
                        isShowConfirmDialog = false
                    )
                }
            )
        }
    }
}

@Composable
fun NoteListItem(
    modifier: Modifier = Modifier,
    info: NoteInfo,
    onItemCardClicked: () -> Unit,
    onItemEditClicked: () -> Unit,
    onItemDeleteClicked: () -> Unit
) {

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    OutlinedCard {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clickable {
                    onItemCardClicked.invoke()
                }
        ) {

            NoteItemTitle(
                modifier = Modifier,
                isLocked = info.isPrivate,
                title = info.title,
                desc = info.timeDesc,
                menuExpanded = menuExpanded,
                onMenuClicked = {
                    menuExpanded = true
                },
                onDismissRequest = {
                    menuExpanded = false
                },
                onEditClicked = {
                    menuExpanded = false
                    onItemEditClicked()
                },
                onDeleteClicked = {
                    menuExpanded = false
                    onItemDeleteClicked()
                }
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(1.dp)
                    .background(
                        color = Color.LightGray
                    ),
            )

            Text(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                text = info.note,
                maxLines = 3
            )

        }
    }
}

@Composable
fun NoteItemTitle(
    modifier: Modifier = Modifier,
    title: String,
    desc: String,
    isLocked: Boolean,
    menuExpanded: Boolean,
    onMenuClicked: () -> Unit,
    onDismissRequest: () -> Unit,
    onEditClicked: (() -> Unit),
    onDeleteClicked: (() -> Unit)
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isLocked) {
                Icon(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Is Private Icon"
                )
            }

            Text(
                modifier = Modifier
                    .weight(1f),
                text = title,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            NoteItemMenu(
                menuExpanded = menuExpanded,
                onDismissRequest = onDismissRequest,
                onEditClicked = onEditClicked,
                onDeleteClicked = onDeleteClicked
            )

            Icon(
                modifier = Modifier
                    .size(width = 24.dp, height = 24.dp)
                    .clickable {
                        onMenuClicked.invoke()
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
fun NoteListScreenEmptyPreview() {
    NoteListScreen(
        modifier = Modifier,
        noteList = emptyList(),
        onItemCardClicked = {},
        onItemEditClicked = {},
        onItemDeleteClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NoteListScreenPreview() {
    val list = mutableListOf<NoteInfo>().apply {
        repeat(15) { index ->
            add(
                NoteInfo(
                    title = "Item Title $index",
                    timeDesc = "xxxx-xx-xx",
                    inputText = "Input Text $index",
                    outputText = "Output Text $index",
                    note = "Test Note Content",
                    isPrivate = (index % 2) == 0
                )
            )
        }
    }

    NoteListScreen(
        noteList = list,
        onItemCardClicked = {},
        onItemEditClicked = {},
        onItemDeleteClicked = {}
    )
}


@Preview(showBackground = true)
@Composable
fun NoteListItemPreview() {

    val noteInfo = NoteInfo(
        id = 0,
        title = "測試標題測試標題測試標題測試標題測試標題",
        timeDesc = "2024/12/25 16:40",
        note = "Note內容",
        inputText = "輸入 ContentText",
        outputText = "輸出 ContentText"
    )

    NoteListItem(
        info = noteInfo,
        onItemCardClicked = {},
        onItemDeleteClicked = {},
        onItemEditClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NoteListItemTitlePreview() {
    NoteItemTitle(
        title = "測試標題測試標題測試標題測試標題測試標題",
        desc = "2024/12/25 16:40",
        isLocked = true,
        menuExpanded = false,
        onMenuClicked = {},
        onDismissRequest = {},
        onEditClicked = {},
        onDeleteClicked = {}
    )
}
