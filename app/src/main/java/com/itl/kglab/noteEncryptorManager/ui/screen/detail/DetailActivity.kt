package com.itl.kglab.noteEncryptorManager.ui.screen.detail

import android.content.ClipData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.input.pointer.pointerInput
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
import com.itl.kglab.noteEncryptorManager.tools.SettingInputRegex
import com.itl.kglab.noteEncryptorManager.ui.component.ContentTextCard
import com.itl.kglab.noteEncryptorManager.ui.component.DescInputItem
import com.itl.kglab.noteEncryptorManager.ui.component.IconButtonEdit
import com.itl.kglab.noteEncryptorManager.ui.component.IconButtonReturn
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.component.TableDivider
import com.itl.kglab.noteEncryptorManager.ui.component.TitleFuncButtons
import com.itl.kglab.noteEncryptorManager.ui.dialog.LoadingDialog
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.detail.DetailViewModel
import com.itl.kglab.noteEncryptorManager.viewmodel.detail.NoteInfoTableData
import com.itl.kglab.noteEncryptorManager.viewmodel.detail.SampleSettingData
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

                    val sampleTableEvent = SampleTableEvent(
                        onInputValueChange = { input ->
                            // 輸入
                            viewModel.setInput(input)
                        },
                        onConvertClicked = {
                            // 轉換
                            viewModel.convertMessage()
                        },
                        onOutputLongClicked = { output ->
                            // 輸出長按
                            val clipData = ClipData.newPlainText(
                                "plain text",
                                output
                            )
                            coroutineScope.launch {
                                clipboardManager.setClipEntry(ClipEntry(clipData))
                            }
                        },
                        onSampleIndexValueChange = { index ->
                            if (index.matches(SettingInputRegex.sampleIndexInputRegex)) {
                                // 取樣起始
                                viewModel.setSampleIndex(index)
                            }
                        },
                        onSampleSizeValueChange = { size ->
                            if (size.matches(SettingInputRegex.sampleSizeInputRegex)) {
                                // 取樣長度
                                viewModel.setSampleSize(size)
                            }
                        },
                        onSampleClicked = {
                            // 取樣
                            viewModel.sampleMessage()
                        }
                    )
                    DetailScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        onBackClicked = {
                            finish()
                        },
                        sampleTableEvent = sampleTableEvent,
                        noteInfoTableData = viewModel.noteInfoState,
                        sampleSettingData = viewModel.settingState
                    )

                    if (viewModel.uiState.isLoading) {
                        LoadingDialog()
                    }

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
    modifier: Modifier,
    onBackClicked: () -> Unit,
    sampleTableEvent: SampleTableEvent,
    noteInfoTableData: NoteInfoTableData = NoteInfoTableData(),
    sampleSettingData: SampleSettingData = SampleSettingData()
) {
    Column(
        modifier = modifier
    ) {

        TitleFuncButtons(
            leftButton = {
                IconButtonReturn { onBackClicked.invoke() }
            },
            rightButton = {
                IconButtonEdit {  }
            }
        )

        DetailTable(
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.screen_table_padding)
                )
                .verticalScroll(rememberScrollState()),
            onNoteLongClicked = { content ->

            },
            sampleTableEvent = sampleTableEvent,
            noteInfoTableData = noteInfoTableData,
            sampleSettingData = sampleSettingData
        )
    }
}

@Composable
fun DetailTable(
    modifier: Modifier = Modifier,
    onNoteLongClicked: (String) -> Unit,
    sampleTableEvent: SampleTableEvent,
    noteInfoTableData: NoteInfoTableData = NoteInfoTableData(),
    sampleSettingData: SampleSettingData = SampleSettingData()
) {
    Column(
        modifier = modifier
    ) {
        NoteInfoDetailTable(
            modifier = Modifier
                .fillMaxWidth(),
            noteInfoTableData = noteInfoTableData,
            onContentLongClicked = onNoteLongClicked,
        )

        SimpleTable(
            modifier = Modifier
                .padding(top = 16.dp),
            sampleSettingData = sampleSettingData,
            sampleEvent = sampleTableEvent
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
    noteInfoTableData: NoteInfoTableData,
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
                    title = noteInfoTableData.title,
                    timeDesc = noteInfoTableData.time,
                    isPrivate = noteInfoTableData.isPrivate
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
                    contentText = noteInfoTableData.note,
                    onContentLongClicked = {
                        onContentLongClicked.invoke(
                            noteInfoTableData.note
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
    modifier: Modifier = Modifier,
    sampleSettingData: SampleSettingData,
    sampleEvent: SampleTableEvent
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
                value = sampleSettingData.input,
                label = stringResource(id = R.string.screen_detail_input),
                labelColor = Color.Gray,
                onValueChange = sampleEvent.onInputValueChange,
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
                onClick = sampleEvent.onConvertClicked
            )

            ContentTextCard(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                contentText = sampleSettingData.output,
                supportingText = "長按可複製"
            )

            TableDivider(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                desc = "取樣設定"
            )

            SampleSettingInput(
                settingData = sampleSettingData,
                sampleTableEvent = sampleEvent
            )

            OutlinedStyleButton(
                modifier = Modifier
                    .padding(
                        horizontal = 8.dp,
                        vertical = 16.dp
                    )
                    .fillMaxWidth(),
                buttonText = "取樣",
                onClick = sampleEvent.onSampleClicked
            )

            ContentTextCard(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                contentText = sampleSettingData.sampledMessage,
                supportingText = "長按可複製"
            )
        }
    }
}

@Composable
private fun SampleSettingInput(
    settingData: SampleSettingData,
    sampleTableEvent: SampleTableEvent
) {
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
            value = settingData.sampleIndex,
            onValueChange = { indexString ->
                sampleTableEvent.onSampleIndexValueChange.invoke(indexString)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            )
        )

        DescInputItem(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            label = "取樣長度",
            supportingText = "請輸入0 ~ 99整數",
            value = settingData.sampleSize,
            onValueChange = { sizeString ->
                sampleTableEvent.onSampleSizeValueChange.invoke(sizeString)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailScreen() {
    DetailScreen(
        modifier = Modifier,
        onBackClicked = {},
        sampleTableEvent = SampleTableEvent(
            onInputValueChange = {},
            onConvertClicked = {},
            onOutputLongClicked = {},
            onSampleIndexValueChange = {},
            onSampleSizeValueChange = {},
            onSampleClicked = {}
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewInformationScreen() {
    DetailTable(
        onNoteLongClicked = {},
        sampleTableEvent = SampleTableEvent(
            onInputValueChange = {},
            onConvertClicked = {},
            onOutputLongClicked = {},
            onSampleIndexValueChange = {},
            onSampleSizeValueChange = {},
            onSampleClicked = {}
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNoteInfoDetailTable() {
    NoteInfoDetailTable(
        modifier = Modifier,
        noteInfoTableData = NoteInfoTableData(
            title = "This is Title container",
            time = "2024-08-11 16:33",
            note = "I wanna tell you something.",
            isPrivate = true
        ),
        onContentLongClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleTable() {
    SimpleTable(
        sampleSettingData = SampleSettingData(),
        sampleEvent = SampleTableEvent(
            onInputValueChange = {},
            onConvertClicked = {},
            onOutputLongClicked = {},
            onSampleIndexValueChange = {},
            onSampleSizeValueChange = {},
            onSampleClicked = {}
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBackTextButton() {
    BackTextButton {}
}

@Preview(showBackground = true)
@Composable
fun PreviewSampleSettingInput() {
    SampleSettingInput(
        settingData = SampleSettingData(),
        sampleTableEvent = SampleTableEvent(
            onInputValueChange = {},
            onConvertClicked = {},
            onOutputLongClicked = {},
            onSampleIndexValueChange = {},
            onSampleSizeValueChange = {},
            onSampleClicked = {}
        )
    )
}

data class SampleTableEvent(
    val onConvertClicked: () -> Unit,
    val onInputValueChange: (String) -> Unit,
    val onOutputLongClicked: (String) -> Unit,
    val onSampleIndexValueChange: (String) -> Unit,
    val onSampleSizeValueChange: (String) -> Unit,
    val onSampleClicked: () -> Unit
)