package com.itl.kglab.noteEncryptorManager.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 8.dp),
            value = "",
            onValueChange = { input ->

            },
            label = {
                Text("輸入")
            }
        )

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            onClick = {  },
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
            )
        ) {
            Text(text = "轉換")
        }

        OutlinedCard(
            modifier = Modifier
                .height(200.dp)
                .padding(vertical = 8.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                text = "Default Text"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val buttonWeight = 1f

            OutlinedButton(
                modifier = Modifier
                    .weight(buttonWeight)
                    .padding(horizontal = 16.dp),
                onClick = {  },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                )
            ) {
                Text(
                    text = "複製"
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(buttonWeight)
                    .padding(horizontal = 16.dp),
                onClick = {  },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                )
            ) {
                Text(text = "儲存")
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(buttonWeight)
                    .padding(horizontal = 16.dp),
                onClick = {  },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                )
            ) {
                Text(text = "清除")
            }
        }
    }
}