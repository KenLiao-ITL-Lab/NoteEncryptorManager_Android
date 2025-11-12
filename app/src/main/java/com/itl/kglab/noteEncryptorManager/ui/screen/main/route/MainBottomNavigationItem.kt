package com.itl.kglab.noteEncryptorManager.ui.screen.main.route

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.itl.kglab.noteEncryptorManager.R
import kotlinx.serialization.Serializable

@Serializable
sealed class MainBottomNavigationItem(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {

    @Serializable
    data object Converter : MainBottomNavigationItem(
        route = "Converter",
        label = R.string.bottom_nav_item_converter,
        icon = R.drawable.icon_converter_24
    )

    @Serializable
    data object NoteList : MainBottomNavigationItem(
        route = "List",
        label = R.string.bottom_nav_item_list,
        icon = R.drawable.icon_list_24
    )

    @Serializable
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