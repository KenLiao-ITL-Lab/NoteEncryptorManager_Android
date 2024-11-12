package com.itl.kglab.noteEncryptorManager.ui.route

import androidx.annotation.DrawableRes
import com.itl.kglab.noteEncryptorManager.R

sealed class MainBottomNavigationItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
) {

    data object Converter : MainBottomNavigationItem(
        route = "Converter",
        label = "Converter",
        icon = R.drawable.icon_enhanced_encryption_24
    )

    data object NoteList : MainBottomNavigationItem(
        route = "NoteList",
        label = "NoteList",
        icon = R.drawable.icon_list_24
    )

    data object Setting : MainBottomNavigationItem(
        route = "Setting",
        label = "Setting",
        icon = R.drawable.icon_settings_24
    )

    companion object {
        fun getItemList(): List<MainBottomNavigationItem> {
            return listOf(
                Converter,
                NoteList,
                Setting
            )
        }
    }
}