package com.itl.kglab.noteEncryptorManager.ui.screen.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.ui.data.DeleteNoteDataEvent
import com.itl.kglab.noteEncryptorManager.ui.dialog.DeleteConfirmDialog
import com.itl.kglab.noteEncryptorManager.viewmodel.main.NoteInfoListInfoData

@Composable
fun NoteListScreen(
    modifier: Modifier = Modifier,
    noteList: List<NoteInfoListInfoData>,
    isBiometricEffective: Boolean = false,
    onBiometricHintClicked: () -> Unit = {},
    onItemCardClicked: (NoteInfoListInfoData) -> Unit,
    onItemEditClicked: (NoteInfoListInfoData) -> Unit,
    onItemDeleteClicked: (NoteInfoListInfoData) -> Unit
) {
    var deleteEventState by remember {
        mutableStateOf(DeleteNoteDataEvent())
    }

    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
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
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(
                        items = noteList,
                        key = { it.id }
                    ) { noteInfo ->
                        NoteListItem(
                            info = noteInfo,
                            showLockIcon = noteInfo.isPrivate && isBiometricEffective,
                            onItemCardClicked = {
                                onItemCardClicked.invoke(noteInfo)
                            },
                            onItemEditClicked = {
                                onItemEditClicked.invoke(noteInfo)
                            },
                            onItemDeleteClicked = {
                                deleteEventState = deleteEventState.copy(
                                    noteInfo = noteInfo,
                                    isShowConfirmDialog = true
                                )
                            }
                        )
                    }
                }
            }
        }

        if (!isBiometricEffective) {
            val bannerShape = RoundedCornerShape(12.dp)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(bannerShape)
                    .clickable(onClick = onBiometricHintClicked),
                shape = bannerShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(id = R.string.screen_list_biometric_disabled_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (deleteEventState.isShowConfirmDialog) {
        deleteEventState.noteInfo?.let { info ->
            DeleteConfirmDialog(
                noteTitle = info.title,
                onConfirmClicked = {
                    onItemDeleteClicked(info)
                    deleteEventState = deleteEventState.copy(
                        noteInfo = null,
                        isShowConfirmDialog = false
                    )
                },
                onDismissRequest = {
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
    info: NoteInfoListInfoData,
    showLockIcon: Boolean = info.isPrivate,
    onItemCardClicked: () -> Unit,
    onItemEditClicked: () -> Unit,
    onItemDeleteClicked: () -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clickable {
                    onItemCardClicked.invoke()
                }
        ) {
            NoteItemTitle(
                modifier = Modifier,
                isLocked = showLockIcon,
                title = info.title,
                desc = info.time,
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

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .height(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                text = info.note,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                text = { Text(text = stringResource(id = R.string.title_func_button_edit)) },
                onClick = {
                    onEditClicked?.invoke()
                }
            )

            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.common_delete)) },
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
        modifier = Modifier.fillMaxSize(),
        noteList = emptyList(),
        isBiometricEffective = false,
        onItemCardClicked = {},
        onItemEditClicked = {},
        onItemDeleteClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NoteListScreenPreview() {
    val list = mutableListOf<NoteInfoListInfoData>().apply {
        repeat(15) { index ->
            add(
                NoteInfoListInfoData(
                    id = index.toLong(),
                    title = "Item Title $index",
                    time = "xxxx-xx-xx",
                    note = "Test Note Content",
                    isPrivate = (index % 2) == 0
                )
            )
        }
    }

    NoteListScreen(
        modifier = Modifier.fillMaxSize(),
        noteList = list,
        isBiometricEffective = true,
        onItemCardClicked = {},
        onItemEditClicked = {},
        onItemDeleteClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NoteListItemPreview() {
    val noteInfo = NoteInfoListInfoData(
        id = 0,
        title = "測試標題測試標題測試標題測試標題測試標題",
        time = "2024/12/25 16:40",
        note = "Note內容"
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
