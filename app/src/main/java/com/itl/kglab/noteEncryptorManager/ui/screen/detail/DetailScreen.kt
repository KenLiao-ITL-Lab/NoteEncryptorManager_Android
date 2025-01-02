package com.itl.kglab.noteEncryptorManager.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.ui.component.ContentTextCard

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    noteInfo: NoteInfo
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        OutlinedCard(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                InformationTitleCard(
                    title = noteInfo.title,
                    timeDesc = noteInfo.timeDesc,
                    isPrivate = noteInfo.isPrivate
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .height(1.dp)
                        .background(
                            color = Color.LightGray
                        ),
                )
                // Note
                ContentTextCard(
                    modifier = Modifier.padding(vertical = 8.dp),
                    label = "備註",
                    contentText = noteInfo.note
                )

                // Input
                ContentTextCard(
                    modifier = Modifier.padding(vertical = 8.dp),
                    label = "輸入",
                    contentText = noteInfo.inputText
                )

                // PW - 點擊可複製
                ContentTextCard(
                    modifier = Modifier.padding(vertical = 8.dp),
                    label = "輸出",
                    contentText = noteInfo.outputText
                )
            }
        }
    }
}

@Composable
fun InformationTitleCard(
    title: String,
    timeDesc: String,
    isPrivate: Boolean
) {
    Row {
        if (isPrivate) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Is Private"
            )
        }

        // Title
        Text(
            modifier = Modifier
                .padding(
                    start = 4.dp
                )
                .weight(1f),
            text = title,
            fontSize = 20.sp,
            maxLines = 1,
        )
    }

    // Date
    Text(
        modifier = Modifier.padding(vertical = 8.dp),
        text = timeDesc,
        color = Color.Gray
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewInfoCardTitle() {

}

@Preview(showBackground = true)
@Composable
fun PreviewInformationScreen() {
    DetailScreen(
        noteInfo = NoteInfo(
            title = "This is Title container",
            timeDesc = "2024-08-11 16:33",
            inputText = "Look!! Input message!",
            outputText = "Woo!! Is output text!",
            note = "I wanna tell you something.",
            isPrivate = true
        )
    )
}