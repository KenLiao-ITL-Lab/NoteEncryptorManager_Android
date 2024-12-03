package com.itl.kglab.noteEncryptorManager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.itl.kglab.noteEncryptorManager.ui.component.MainBottomNavigationBar
import com.itl.kglab.noteEncryptorManager.ui.route.MainRoute
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

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
                    MainRoute(
                        navHostController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        viewModel = mainViewModel
                    )
                }
            }
        }
    }
}
