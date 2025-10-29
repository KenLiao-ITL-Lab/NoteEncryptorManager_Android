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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.ui.component.ContentTextCard
import com.itl.kglab.noteEncryptorManager.ui.component.DescInputItem
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
                    .padding(16.dp)
            ) {

                InformationTitleCard(
                    title = noteInfo.title,
                    timeDesc = noteInfo.timeDesc,
                    isPrivate = noteInfo.isPrivate
                )

                TableDivider(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    desc = "長按內容即可複製"
                )

                // Note
                ContentTextCard(
                    modifier = Modifier.padding(vertical = 8.dp),
                    titleLabel = "備註",
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
                    titleLabel = "輸入",
                    contentText = noteInfo.inputText,
                    onContentLongClicked = {
                        onContentLongClicked.invoke(
                            noteInfo.inputText
                        )
                    }
                )

                TableDivider(
                    modifier = Modifier
                        .padding(top = 24.dp),
                    desc = "取樣設定"
                )

                DescInputItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    value = config.sampleSizeValue,
                    label = stringResource(id = R.string.screen_setting_size_label),
                    labelColor = Color.Gray,
                    onValueChange = config.onSampleSizeChange,
                    supportingText = stringResource(id = R.string.screen_setting_size_desc),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    )
                )

                DescInputItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    value = config.indexValue,
                    label = stringResource(id = R.string.screen_setting_index_label),
                    labelColor = Color.Gray,
                    onValueChange = config.onIndexChange,
                    supportingText = stringResource(id = R.string.screen_setting_index_desc),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    )
                )

                // PW - 點擊可複製
                ContentTextCard(
                    modifier = Modifier
                        .padding(vertical = 24.dp),
                    titleLabel = "輸出",
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
        config = EditorSettingConfig(),
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

data class EditorSettingConfig(
    val sampleSizeValue: String = "",
    val onSampleSizeChange: (String) -> Unit = {},
    val indexValue: String = "",
    val onIndexChange: (String) -> Unit = {},
)