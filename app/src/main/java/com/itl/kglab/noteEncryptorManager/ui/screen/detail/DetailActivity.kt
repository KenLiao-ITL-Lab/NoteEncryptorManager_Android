package com.itl.kglab.noteEncryptorManager.ui.screen.detail

import android.content.ClipData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.ui.component.ContentTextCard
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.detail.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : ComponentActivity() {

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initViewData()
        setContent {

            val clipboardManager = LocalClipboardManager.current

            NoteEncryptorManagerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    DetailScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(
                                horizontal = dimensionResource(id = R.dimen.screen_table_padding)
                            ),
                        noteInfo = viewModel.viewState.noteInfo,
                        onBackClicked = {
                            finish()
                        },
                        onContentLongClicked = { content ->
                            val clipData = ClipData.newPlainText(
                                "plain text",
                                content
                            )

                            clipboardManager.setClip(
                                ClipEntry(clipData)
                            )
                        }
                    )
                }
            }
        }
    }

    private fun initViewData() {
        val bundle = intent.extras
        bundle?.let {
            val id = it.getLong(ARG_ID)
            viewModel.getNoteInfoById(id)
        }
    }

    companion object {
        const val ARG_ID = "ID_LONG"
    }
}

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    noteInfo: NoteInfo,
    onBackClicked: () -> Unit,
    onContentLongClicked: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        BackTextButton(
            onBackClicked = onBackClicked
        )

        NoteInfoDetailTable(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            noteInfo = noteInfo,
            onContentLongClicked = onContentLongClicked
        )
    }
}

@Composable
fun InformationTitleCard(
    title: String,
    timeDesc: String,
    isPrivate: Boolean
) {
    Row {
        if (isPrivate) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Is Private"
            )
        }

        // Title
        Text(
            modifier = Modifier
                .padding(
                    start = 4.dp
                )
                .weight(1f),
            text = title,
            fontSize = 20.sp,
        )
    }

    // Date
    Text(
        modifier = Modifier.padding(vertical = 8.dp),
        text = timeDesc,
        color = Color.Gray
    )
}

@Composable
fun BackTextButton(
    onBackClicked: () -> Unit
) {
    TextButton(
        onClick = onBackClicked
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back"
            )
            Text(
                text = "返回"
            )
        }
    }
}

@Composable
fun NoteInfoDetailTable(
    modifier: Modifier,
    noteInfo: NoteInfo,
    onContentLongClicked: (String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                InformationTitleCard(
                    title = noteInfo.title,
                    timeDesc = noteInfo.timeDesc,
                    isPrivate = noteInfo.isPrivate
                )

                Row(
                    modifier = Modifier,
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
                        text = "長按內容即可複製"
                    )
                }

                // Note
                ContentTextCard(
                    modifier = Modifier.padding(vertical = 8.dp),
                    label = "備註",
                    contentText = noteInfo.note,
                    onContentLongClicked = {
                        onContentLongClicked.invoke(
                            noteInfo.note
                        )
                    }
                )

                // Input
                ContentTextCard(
                    modifier = Modifier.padding(vertical = 8.dp),
                    label = "輸入",
                    contentText = noteInfo.inputText,
                    onContentLongClicked = {
                        onContentLongClicked.invoke(
                            noteInfo.inputText
                        )
                    }
                )

                // PW - 點擊可複製
                ContentTextCard(
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    label = "輸出",
                    contentText = noteInfo.outputText,
                    onContentLongClicked = {
                        onContentLongClicked.invoke(
                            noteInfo.outputText
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBackTextButton() {
    BackTextButton {}
}

@Preview(showBackground = true)
@Composable
fun PreviewNoteInfoDetailTable() {
    NoteInfoDetailTable(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        noteInfo = NoteInfo(
            title = "This is Title container",
            timeDesc = "2024-08-11 16:33",
            inputText = "Look!! Input message!",
            outputText = "Woo!! Is output text!",
            note = "I wanna tell you something.",
            isPrivate = true
        ),
        onContentLongClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewInformationScreen() {
    DetailScreen(
        noteInfo = NoteInfo(
            title = "This is Title container",
            timeDesc = "2024-08-11 16:33",
            inputText = "Look!! Input message!",
            outputText = "Woo!! Is output text!",
            note = "I wanna tell you something.",
            isPrivate = true
        ),
        onBackClicked = {},
        onContentLongClicked = {}
    )
}