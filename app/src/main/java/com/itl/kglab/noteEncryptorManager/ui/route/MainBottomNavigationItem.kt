package com.itl.kglab.noteEncryptorManager.ui.route

import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.itl.kglab.noteEncryptorManager.R

sealed class MainBottomNavigationItem(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {

    data object Converter : MainBottomNavigationItem(
        route = "Converter",
        label = R.string.bottom_nav_item_converter,
        icon = R.drawable.icon_enhanced_encryption_24
    )

    data object NoteList : MainBottomNavigationItem(
        route = "List",
        label = R.string.bottom_nav_item_list,
        icon = R.drawable.icon_list_24
    )

    data object Setting : MainBottomNavigationItem(
        route = "Setting",
        label = R.string.bottom_nav_item_setting,
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