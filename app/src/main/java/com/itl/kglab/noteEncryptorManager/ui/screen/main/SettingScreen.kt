package com.itl.kglab.noteEncryptorManager.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    onSaveSettingClicked: (SettingScreenInfo) -> Unit,
    onToggleBiometric: (Boolean) -> Unit
) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(settingInfo.algorithmIndex) }
    var isBiometricEnabled by rememberSaveable { mutableStateOf(settingInfo.isBiometricEnabled) }

    LaunchedEffect(settingInfo.algorithmIndex) {
        selectedItemIndex = settingInfo.algorithmIndex
    }

    LaunchedEffect(settingInfo.isBiometricEnabled) {
        isBiometricEnabled = settingInfo.isBiometricEnabled
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
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

        OutlinedCard(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.screen_setting_biometric_toggle_label),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isBiometricEnabled,
                    onCheckedChange = { checked ->
                        onToggleBiometric(checked)
                    }
                )
            }
        }

        SettingButtonGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            onConfirmClicked = {
                val selectedAlgorithm = hashTypeList.getOrNull(selectedItemIndex) ?: return@SettingButtonGroup
                onSaveSettingClicked(
                    SettingScreenInfo(
                        algorithmIndex = selectedItemIndex,
                        isBiometricEnabled = isBiometricEnabled,
                        info = SettingInfo(
                            algorithmName = selectedAlgorithm,
                            isBiometricEnabled = isBiometricEnabled
                        ),
                    )
                )
            },
            onCancelClicked = {
                selectedItemIndex = settingInfo.algorithmIndex
                isBiometricEnabled = settingInfo.isBiometricEnabled
            }
        )
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
    val selectedItemName = list.getOrNull(selectedItemIndex).orEmpty()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                value = selectedItemName,
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
        onSaveSettingClicked = {},
        onToggleBiometric = {}
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
