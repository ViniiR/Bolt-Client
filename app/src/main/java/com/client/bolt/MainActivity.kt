package com.client.bolt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.client.bolt.ui.theme.BoltTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoltTheme {
                App(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.BOOKS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            IconButton(
                onClick = {

                },
            ) {
                Icon(Icons.Default.Create, contentDescription = "Filter")
            }
        },
        bottomBar = {
            BottomBar(
                selectedDestination = selectedDestination,
                onClick = { destination: Destination, index: Int ->
                    navController.navigate(route = destination.route)
                    selectedDestination = index
                },
            )
        }
    ) { paddingValues ->
        AppNavHost(
            navController,
            startDestination,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

//@Preview(showBackground = true, widthDp = 50, heightDp = 79)
//@Composable
//fun AppPreview() {
//    BoltTheme {
//        App(
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}