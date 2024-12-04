package com.itl.kglab.noteEncryptorManager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.screen.data.SettingScreenInfo

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    hashTypeList: List<String>,
    settingInfo: SettingScreenInfo,
    onSaveSettingClicked: (SettingScreenInfo) -> Unit
) {

    var prefixInput by rememberSaveable {
        mutableStateOf(settingInfo.prefixText)
    }

    var suffixInput by rememberSaveable {
        mutableStateOf(settingInfo.suffixText)
    }

    var sampleSizeInput by rememberSaveable {
        mutableIntStateOf(settingInfo.samplingSize)
    }

    var indexInput by rememberSaveable {
        mutableIntStateOf(settingInfo.sampleIndex)
    }

    val decorateRegex = remember {
        Regex("^[a-zA-Z]{0,10}\$")
    }

    val sampleSizeRegex = remember {
        Regex("^[0-9]?\$")
    }

    val indexRegex = remember {
        Regex("^[0-9]{0,2}$")
    }

    var selectedItemIndex by remember {
        mutableIntStateOf(settingInfo.algorithmIndex)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedCard(
            modifier = Modifier
                .padding(8.dp)
        ) {
            SettingTable(
                hashTypeList = hashTypeList,
                prefixValue = prefixInput,
                onPrefixChange = {
                    if (decorateRegex.matches(it)) {
                        prefixInput = it
                    }
                },
                suffixValue = suffixInput,
                onSuffixChange = {
                    if (decorateRegex.matches(it)) {
                        suffixInput = it
                    }
                },
                sampleSizeValue = sampleSizeInput.toString(),
                onSampleSizeChange = {
                    if (sampleSizeRegex.matches(it)) {
                        sampleSizeInput = if (it.isBlank()) 0 else it.toInt()
                    }
                },
                indexValue = indexInput.toString(),
                onIndexChange = {
                    if (indexRegex.matches(it)) {
                        indexInput = if (it.isBlank()) 0 else it.toInt()
                    }
                },
                onSelectedItemIndex = { index ->
                    selectedItemIndex = index
                },
                selectedItemIndex = selectedItemIndex
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            SettingButtonGroup(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onConfirmClicked = {
                    onSaveSettingClicked(
                        SettingScreenInfo(
                            algorithmName = hashTypeList[selectedItemIndex],
                            algorithmIndex = selectedItemIndex,
                            prefixText = prefixInput,
                            suffixText = suffixInput,
                            samplingSize = sampleSizeInput,
                            sampleIndex = indexInput
                        )
                    )
                },
                onCancelClicked = {
                    selectedItemIndex = settingInfo.algorithmIndex
                    prefixInput = settingInfo.prefixText
                    suffixInput = settingInfo.suffixText
                    sampleSizeInput = settingInfo.samplingSize
                    indexInput = settingInfo.sampleIndex
                }
            )
        }
    }
}

@Composable
fun SettingTable(
    hashTypeList: List<String>,
    prefixValue: String = "",
    onPrefixChange: (String) -> Unit = {},
    suffixValue: String = "",
    onSuffixChange: (String) -> Unit = {},
    sampleSizeValue: String = "",
    onSampleSizeChange: (String) -> Unit = {},
    indexValue: String = "",
    onIndexChange: (String) -> Unit = {},
    selectedItemIndex: Int,
    onSelectedItemIndex: (Int) -> Unit,
) {

    val itemPadding = 8.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        DropMenuItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = itemPadding),
            list = hashTypeList,
            label = "選擇演算法",
            selectedItemIndex = selectedItemIndex,
            onSelectedItemIndex = onSelectedItemIndex
        )

        SettingInputItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = itemPadding),
            value = prefixValue,
            label = "前綴",
            onValueChange = onPrefixChange,
            supportingText = "長度限制為10",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        SettingInputItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = itemPadding),
            value = suffixValue,
            label = "後綴",
            onValueChange = onSuffixChange,
            supportingText = "長度限制為10",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        SettingInputItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = itemPadding),
            value = sampleSizeValue,
            label = "取樣長度",
            onValueChange = onSampleSizeChange,
            supportingText = "請輸入0~9數字",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        SettingInputItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = itemPadding),
            value = indexValue,
            label = "起始位置",
            onValueChange = onIndexChange,
            supportingText = "請輸入0~99數字",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Composable
fun SettingButtonGroup(
    modifier: Modifier = Modifier,
    onConfirmClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {

        OutlinedStyleButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            onClick = onConfirmClicked,
            buttonText = "儲存"
        )

        OutlinedStyleButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            onClick = onCancelClicked,
            buttonText = "取消"
        )
    }
}

@Composable
fun SettingInputItem(
    modifier: Modifier = Modifier,
    value: String = "",
    label: String = "",
    supportingText: String = "",
    onValueChange: (String) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        label = { Text(text = label)},
        supportingText = if (supportingText.isBlank()) {
            null
        } else {
            {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = supportingText)
                }
            }
        },
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropMenuItem(
    modifier: Modifier = Modifier,
    label: String,
    list: List<String> = emptyList(),
    selectedItemIndex: Int,
    onSelectedItemIndex: (Int) -> Unit
) {

    val defaultItemName = if (list.isEmpty()) "" else list[selectedItemIndex]

    var expanded by remember { mutableStateOf(false) }

    val textFieldState = rememberTextFieldState(defaultItemName)

    Column(
        modifier = modifier
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded}
        ) {

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                value = defaultItemName,
                label = { Text(text = label) },
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                supportingText = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(text = "演算法會影響輸出結果")
                    }
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                list.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd(item)
                            onSelectedItemIndex(index)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingScreen() {
    SettingScreen(
        modifier = Modifier.fillMaxSize(),
        hashTypeList = emptyList(),
        settingInfo = SettingScreenInfo(),
        onSaveSettingClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewDropMenu() {
    DropMenuItem(
        label = "選項說明",
        selectedItemIndex = 0,
        onSelectedItemIndex = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewInputItem() {
    SettingInputItem(
        value = "Preview Input",
        label = "Preview Label",
        supportingText = "Preview SupportingText"
    )
}