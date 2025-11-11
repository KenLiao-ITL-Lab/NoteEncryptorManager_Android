package com.itl.kglab.noteEncryptorManager.ui.screen.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.itl.kglab.noteEncryptorManager.manager.BiometricPromptManager
import com.itl.kglab.noteEncryptorManager.ui.screen.main.route.MainBottomNavigationBar
import com.itl.kglab.noteEncryptorManager.ui.screen.main.route.MainRoute
import com.itl.kglab.noteEncryptorManager.ui.theme.NoteEncryptorManagerTheme
import com.itl.kglab.noteEncryptorManager.viewmodel.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val bioAuthManager by lazy {
        BiometricPromptManager(this)
    }

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
                        viewModel = mainViewModel,
                        bioAuthManager = bioAuthManager
                    )
                }
            }
        }
    }
}
