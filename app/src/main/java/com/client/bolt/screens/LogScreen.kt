package com.client.bolt.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.client.bolt.views.BookView

@Preview(showSystemUi = true)
@Composable
fun LogScreen(view: BookView = viewModel()) {
    if (view.logList.isEmpty()) {
        return Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No Logs yet, yay!",
                textAlign = TextAlign.Center
            )
        }
    }
    Column (
        Modifier.padding(10.dp),
    ) {
        view.logList.forEach { log ->
            Text(
                log,
            )
        }
    }
}