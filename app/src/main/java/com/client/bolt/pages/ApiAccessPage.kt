package com.client.bolt.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.bolt.TopBar
import com.client.bolt.datastores.ApiSettingsDataStore
import kotlinx.coroutines.launch

@Composable
@Preview(showSystemUi = true)
fun ApiSettingsPage(
    modifier: Modifier = Modifier,
    menuButtonHandler: () -> Unit = {}
) {
    Scaffold(
        modifier,
        topBar = {
            TopBar(
                menuHandler = menuButtonHandler,
                title = "Api Settings"
            )
        }
    ) { paddingValues ->
        ApiAccessScreen(Modifier.padding(paddingValues))
    }
}
@Composable
fun ConfigTitle(text: String) {
    Text(
        text,
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        textAlign = TextAlign.Left,
    )
}

@Preview(showSystemUi = true)
@Composable
fun ApiAccessScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val dataStore = remember { ApiSettingsDataStore(context) }

    val storedUrl by dataStore.url.collectAsStateWithLifecycle(null)
    val storedUsername by dataStore.username.collectAsStateWithLifecycle(null)
    val storedPassword by dataStore.password.collectAsStateWithLifecycle(null)

    var apiUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var authUsername by rememberSaveable { mutableStateOf<String?>(null) }
    var authPassword by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(storedUrl) {
        if (apiUrl == null) {
            apiUrl = storedUrl
        }
        if (authUsername == null) {
            authUsername = storedUsername
        }
        if (authPassword == null) {
            authPassword = storedPassword
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(50.dp, 2.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ConfigTitle("Api")
        OutlinedTextField(
            value = apiUrl ?: "",
            onValueChange = {
                apiUrl = it.trim()
            },
            label = {
                Text("Api Url")
            },
            supportingText = {
                Text("Include Port")
            },
            placeholder = {
                Text("https://api.dev:5001")
            }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HorizontalDivider(Modifier.fillMaxWidth(), 2.dp, MaterialTheme.colorScheme.tertiary)
            ConfigTitle("Authentication")
        }
        OutlinedTextField(
            value = authUsername ?: "",
            onValueChange = {
                authUsername = it.trim()
            },
            label = {
                Text("Username")
            },
            placeholder = {
                Text("vinii")
            }
        )
        OutlinedTextField(
            value = authPassword ?: "",
            onValueChange = {
                authPassword = it.trim()
            },
            label = {
                Text("Password")
            },
            placeholder = {
                Text("********")
            }
        )
        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        dataStore.updateUrl(apiUrl ?: "")
                        dataStore.updateUsername(authUsername ?: "")
                        dataStore.updatePassword(authPassword ?: "")
                    }
                },
                Modifier.width(150.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Done, "Save Settings")
                Spacer(Modifier.width(10.dp))
                Text("Save")
            }
        }
    }
}