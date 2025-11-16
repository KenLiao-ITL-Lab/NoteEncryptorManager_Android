package com.itl.kglab.noteEncryptorManager.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.R
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import com.itl.kglab.noteEncryptorManager.ui.component.OutlinedStyleButton
import com.itl.kglab.noteEncryptorManager.ui.screen.main.data.SettingScreenInfo

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    hashTypeList: List<String>,
    settingInfo: SettingScreenInfo,
    onSaveSettingClicked: (SettingScreenInfo) -> Unit
) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(settingInfo.algorithmIndex) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedCard(
            modifier = Modifier
                .padding(vertical = 8.dp)
        ) {
            SettingTable(
                hashTypeList = hashTypeList,
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
                            ),
                        )
                    )
                },
                onCancelClicked = {
                    selectedItemIndex = settingInfo.algorithmIndex
                }
            )
        }
    }
}

@Composable
fun SettingTable(
    hashTypeList: List<String>,
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