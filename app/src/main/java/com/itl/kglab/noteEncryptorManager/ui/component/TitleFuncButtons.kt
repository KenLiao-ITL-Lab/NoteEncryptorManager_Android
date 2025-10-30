package com.itl.kglab.noteEncryptorManager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itl.kglab.noteEncryptorManager.R

@Composable
fun TitleFuncButtons(
    leftButton: @Composable (() -> Unit)?,
    rightButton: @Composable (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            leftButton?.invoke()
        }
        Column(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            rightButton?.invoke()
        }
    }
}

@Composable
fun IconButtonReturn(
    onClick: (() -> Unit)?
) {
    IconButton(
        stringResource(R.string.title_func_button_return),
        painterResource(R.drawable.icon_return_24),
        onClick = onClick
    )
}

@Composable
fun IconButtonEdit(
    onClick: (() -> Unit)?
) {
    IconButton(
        stringResource(R.string.title_func_button_edit),
        painterResource(R.drawable.icon_edit_24),
        onClick = onClick
    )
}

@Composable
fun IconButtonSave(
    onClick: (() -> Unit)?
) {
    IconButton(
        stringResource(R.string.title_func_button_save),
        painterResource(R.drawable.icon_save_24),
        onClick = onClick
    )
}

@Composable
private fun IconButton(
    text: String,
    painter: Painter,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .clickable {
                onClick?.invoke()
            },
        verticalAlignment = Alignment.CenterVertically,
    ){
        Icon(
            modifier = Modifier
                .padding(start = 8.dp),
            painter = painter,
            contentDescription = "Return Icon"
        )
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = text
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTitleFuncButtons() {
    TitleFuncButtons(
        leftButton = {
            IconButton(
                stringResource(R.string.title_func_button_return),
                painterResource(R.drawable.icon_return_24)
            )
        },
        rightButton = {
            IconButton(
                stringResource(R.string.title_func_button_edit),
                painterResource(R.drawable.icon_edit_24)
            )
        }
    )
}