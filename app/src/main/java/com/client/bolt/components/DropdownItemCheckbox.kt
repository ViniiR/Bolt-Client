package com.client.bolt.components

import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DropdownItemCheckbox(
    text: String,
    state: Boolean,
    onChangeState: (Boolean) -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(text)
        },
        leadingIcon = {
            Checkbox(
                checked = state,
                onCheckedChange = null
            )
        },
        onClick = {
            onChangeState(!state)
        }
    )
}

