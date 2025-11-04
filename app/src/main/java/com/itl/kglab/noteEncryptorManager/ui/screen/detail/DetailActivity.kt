package com.itl.kglab.noteEncryptorManager.ui.screen.detail

import android.content.ClipData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.ui.component.ContentTextCard
import com.itl.kglab.noteEncryptorManager.ui.component.DescInputItem
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.component.TableDivider
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.detail.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : ComponentActivity() {

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initViewData()
        setContent {
            val clipboardManager = LocalClipboard.current
            val coroutineScope = rememberCoroutineScope()

            NoteEncryptorManagerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    DetailScreen(
                        modifier = Modifier
                            .fillMaxWidth()
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
                            coroutineScope.launch {
                                clipboardManager.setClipEntry(ClipEntry(clipData))
                            }
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
    onContentLongClicked: (String) -> Unit,
) {

    Column(
        modifier = modifier
    ) {

        BackTextButton(
            onBackClicked = onBackClicked
        )

        NoteInfoDetailTable(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            noteInfo = noteInfo,
            config = EditorSettingConfig(),
            onContentLongClicked = onContentLongClicked,
        )

        SimpleTable(
            modifier = Modifier
                .padding(top = 16.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp),
        textAlign = TextAlign.End,
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
    config: EditorSettingConfig,
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
                    .background(color = Color.White)
                    .padding(16.dp)
            ) {

                InformationTitleCard(
                    title = noteInfo.title,
                    timeDesc = noteInfo.timeDesc,
                    isPrivate = noteInfo.isPrivate
                )

                TableDivider(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    desc = ""
                )

                // Note
                ContentTextCard(
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    contentText = "備註內容",
                    onContentLongClicked = {
                        onContentLongClicked.invoke(
                            noteInfo.note
                        )
                    }
                )

                // 是否鎖定Checkbox

            }
        }
    }
}

@Composable
private fun SimpleTable(
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            TableDivider(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 24.dp),
                desc = "轉換器",
            )

            DescInputItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                value = "密文內容",
                label = stringResource(id = R.string.screen_detail_input),
                labelColor = Color.Gray,
                onValueChange = {},
                supportingText = "請輸入密文內容",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedStyleButton(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                buttonText = "轉換",
                onClick = {}
            )

            ContentTextCard(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                contentText = "轉換內容",
                supportingText = "長按可複製"
            )

            TableDivider(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                desc = "取樣設定"
            )

            SampleSettingInput()

            OutlinedStyleButton(
                modifier = Modifier
                    .padding(
                        horizontal = 8.dp,
                        vertical = 16.dp
                    )
                    .fillMaxWidth(),
                buttonText = "取樣"
            )
        }
    }
}

@Composable
private fun SampleSettingInput() {
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
    ) {
        DescInputItem(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
            label = "取樣起始",
            supportingText = "請輸入0 ~ 99整數",
            onValueChange = {}
        )

        DescInputItem(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            label = "取樣長度",
            supportingText = "請輸入0 ~ 99整數",
            onValueChange = {}
        )
    }
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
        config = EditorSettingConfig(),
        onContentLongClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleTable() {
    SimpleTable()
}

@Preview(showBackground = true)
@Composable
fun PreviewBackTextButton() {
    BackTextButton {}
}

@Preview(showBackground = true)
@Composable
fun PreviewSampleSettingInput() {
    SampleSettingInput()
}

data class EditorSettingConfig(
    val sampleSizeValue: String = "",
    val onSampleSizeChange: (String) -> Unit = {},
    val indexValue: String = "",
    val onIndexChange: (String) -> Unit = {},
)