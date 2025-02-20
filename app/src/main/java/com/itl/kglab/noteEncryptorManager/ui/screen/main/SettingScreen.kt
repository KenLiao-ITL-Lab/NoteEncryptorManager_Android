package com.itl.kglab.noteEncryptorManager.ui.screen.main

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.screen.main.data.SettingScreenInfo
import com.itl.kglab.noteEncryptorManager.tools.SettingInputRegex

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    hashTypeList: List<String>,
    settingInfo: SettingScreenInfo,
    onSaveSettingClicked: (SettingScreenInfo) -> Unit
) {
    var prefixInput by rememberSaveable { mutableStateOf(settingInfo.info.prefixText) }
    var suffixInput by rememberSaveable { mutableStateOf(settingInfo.info.suffixText) }
    var sampleSizeInput by rememberSaveable { mutableStateOf(settingInfo.info.samplingSize.toString()) }
    var indexInput by rememberSaveable { mutableStateOf(settingInfo.info.sampleIndex.toString()) }
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(settingInfo.algorithmIndex) }

    val regex = remember { SettingInputRegex() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val config = SettingTableConfig(
            prefixValue = prefixInput,
            onPrefixChange = {
                if (regex.decorateRegex.matches(it)) {
                    prefixInput = it
                }
            },
            suffixValue = suffixInput,
            onSuffixChange = {
                if (regex.decorateRegex.matches(it)) {
                    suffixInput = it
                }
            },
            sampleSizeValue = sampleSizeInput.toString(),
            onSampleSizeChange = {
                if (regex.sampleSizeRegex.matches(it)) {
                    sampleSizeInput = it
                }
            },
            indexValue = indexInput.toString(),
            onIndexChange = {
                if (regex.indexRegex.matches(it)) {
                    indexInput = it
                }
            }
        )

        OutlinedCard(
            modifier = Modifier
                .padding(vertical = 8.dp)
        ) {
            SettingTable(
                hashTypeList = hashTypeList,
                tableConfig = config,
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
                            algorithmIndex = selectedItemIndex,
                            info = SettingInfo(
                                algorithmName = hashTypeList[selectedItemIndex],
                                prefixText = prefixInput,
                                suffixText = suffixInput,
                                samplingSize = if (sampleSizeInput.isBlank()) 0 else sampleSizeInput.toInt(),
                                sampleIndex = if (indexInput.isBlank()) 0 else indexInput.toInt()
                            ),
                        )
                    )
                },
                onCancelClicked = {
                    selectedItemIndex = settingInfo.algorithmIndex
                    prefixInput = settingInfo.info.prefixText
                    suffixInput = settingInfo.info.suffixText
                    sampleSizeInput = settingInfo.info.samplingSize.toString()
                    indexInput = settingInfo.info.sampleIndex.toString()
                }
            )
        }
    }
}

@Composable
fun SettingTable(
    hashTypeList: List<String>,
    tableConfig: SettingTableConfig,
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
            label = stringResource(id = R.string.screen_setting_algorithm_menu_label),
            selectedItemIndex = selectedItemIndex,
            onSelectedItemIndex = onSelectedItemIndex
        )

        SettingInputItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = itemPadding),
            value = tableConfig.prefixValue,
            label = stringResource(id = R.string.screen_setting_prefix_label),
            labelColor = Color.Gray,
            onValueChange = tableConfig.onPrefixChange,
            supportingText = stringResource(id = R.string.screen_setting_prefix_desc),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        SettingInputItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = itemPadding),
            value = tableConfig.suffixValue,
            label = stringResource(id = R.string.screen_setting_suffix_label),
            labelColor = Color.Gray,
            onValueChange = tableConfig.onSuffixChange,
            supportingText = stringResource(id = R.string.screen_setting_suffix_desc),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        SettingInputItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = itemPadding),
            value = tableConfig.sampleSizeValue,
            label = stringResource(id = R.string.screen_setting_size_label),
            labelColor = Color.Gray,
            onValueChange = tableConfig.onSampleSizeChange,
            supportingText = stringResource(id = R.string.screen_setting_size_desc),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        SettingInputItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = itemPadding),
            value = tableConfig.indexValue,
            label = stringResource(id = R.string.screen_setting_index_label),
            labelColor = Color.Gray,
            onValueChange = tableConfig.onIndexChange,
            supportingText = stringResource(id = R.string.screen_setting_index_desc),
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
    onConfirmClicked: () -> Unit,
    onCancelClicked: () -> Unit
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
            buttonText = stringResource(id = R.string.screen_setting_save_button)
        )

        OutlinedStyleButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            onClick = onCancelClicked,
            buttonText = stringResource(id = R.string.screen_setting_reset)
        )
    }
}

@Composable
fun SettingInputItem(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    labelColor: Color = Color.Unspecified,
    supportingText: String = "",
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        label = { Text(
            text = label,
            color = labelColor
        )},
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
    list: List<String>,
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
                        Text(text = stringResource(id = R.string.screen_setting_algorithm_desc))
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

class SettingTableConfig(
    val prefixValue: String = "",
    val onPrefixChange: (String) -> Unit = {},
    val suffixValue: String = "",
    val onSuffixChange: (String) -> Unit = {},
    val sampleSizeValue: String = "",
    val onSampleSizeChange: (String) -> Unit = {},
    val indexValue: String = "",
    val onIndexChange: (String) -> Unit = {},
)

@Preview(showBackground = true)
@Composable
fun PreviewSettingScreen() {
    SettingScreen(
        modifier = Modifier.fillMaxSize(),
        hashTypeList = emptyList(),
        settingInfo = SettingScreenInfo(info = SettingInfo()),
        onSaveSettingClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewDropMenu() {
    DropMenuItem(
        label = "選項說明",
        list = emptyList(),
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
        supportingText = "Preview SupportingText",
        onValueChange = {}
    )
}