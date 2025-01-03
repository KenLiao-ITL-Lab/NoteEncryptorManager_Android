package com.itl.kglab.noteEncryptorManager.ui.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
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

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteEncryptorManagerTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics {
                            testTagsAsResourceId = true
                        },
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
