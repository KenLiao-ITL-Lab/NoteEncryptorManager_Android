package com.itl.kglab.noteEncryptorManager.ui.route

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.itl.kglab.noteEncryptorManager.ui.screen.ConverterScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.NoteListScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.SettingScreen
import com.itl.kglab.noteEncryptorManager.viewmodel.MainViewModel

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = MainBottomNavigationItem.Converter.route
    ) {

        val screenModifier = Modifier.padding(horizontal = 16.dp)

        composable(MainBottomNavigationItem.Converter.route) {
            ConverterScreen(
                modifier = screenModifier,
                resultText = viewModel.resultState,
                onConvertClicked = { input ->
                    viewModel.convertInput(input)
                },
                onDuplicateClicked = {
                    viewModel.duplicateResult()
                },
                onSaveClicked = {
                    viewModel.saveResult()
                },
                onClearClicked = {
                    viewModel.clear()
                }
            )
        }
        composable(MainBottomNavigationItem.NoteList.route) {
            NoteListScreen(
                modifier = screenModifier
            )
        }
        composable(MainBottomNavigationItem.Setting.route) {
            SettingScreen(
                modifier = screenModifier
            )
        }
    }
}