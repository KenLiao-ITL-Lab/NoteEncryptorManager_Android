package com.itl.kglab.noteEncryptorManager.ui.screen.main.route

import android.content.res.Configuration
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainBottomNavigationBar(navController: NavController) {
    NavigationBar {
        val keyboardManager = LocalSoftwareKeyboardController.current

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        MainBottomNavigationItem.getItemList().forEach { item ->
            NavigationBarItem(
                selected = item.route == currentRoute,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        keyboardManager?.hide()
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        stringResource(id = item.label)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun BottomNavigationPreview() {
    NavigationBar {
        val currentRoute = "Converter"
        MainBottomNavigationItem.getItemList().forEach { item ->
            NavigationBarItem(
                selected = item.route == currentRoute,
                onClick = {  },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        stringResource(id = item.label)
                    )
                }
            )
        }
    }
}