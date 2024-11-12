package com.itl.kglab.noteEncryptorManager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itl.kglab.noteEncryptorManager.ui.component.MainBottomNavigationBar
import com.itl.kglab.noteEncryptorManager.ui.route.MainBottomNavigationItem
import com.itl.kglab.noteEncryptorManager.ui.screen.ConverterScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.NoteListScreen
import com.itl.kglab.noteEncryptorManager.ui.screen.SettingScreen
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteEncryptorManagerTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MainBottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        navController = navController,
                        startDestination = MainBottomNavigationItem.Converter.route
                    ) {
                        composable(MainBottomNavigationItem.Converter.route) {
                            ConverterScreen()
                        }
                        composable(MainBottomNavigationItem.NoteList.route) {
                            NoteListScreen()
                        }
                        composable(MainBottomNavigationItem.Setting.route) {
                            SettingScreen()
                        }
                    }
                }
            }
        }
    }
}
