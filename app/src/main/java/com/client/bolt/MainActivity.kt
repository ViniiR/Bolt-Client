package com.client.bolt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.client.bolt.pages.ApiSettingsPage
import com.client.bolt.pages.MainPage
import com.client.bolt.ui.theme.BoltTheme
import com.client.bolt.views.BookView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val booksView: BookView by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoltTheme {
                App(
                    modifier = Modifier.fillMaxSize(),
                    booksView,
                )
            }
        }
    }
}

enum class DrawerDestinations(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
) {
    Home("home", "Home", Icons.Default.Home, "Home Page"),
    ApiSettings("api", "Api Settings", Icons.Default.Build, "Api Settings"),
    Settings("settings", "Settings", Icons.Default.Settings, "Settings"),
}

@Preview(showBackground = true)
@Composable
fun App(
    modifier: Modifier = Modifier,
    bookView: BookView = viewModel(),
) {
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val navController = rememberNavController()
    val startDestination = DrawerDestinations.Home
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(.8f),
            ) {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Menu",
                        Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider()

                    DrawerDestinations.entries.forEachIndexed { index, destination ->
                        NavigationDrawerItem(
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier,
                            label = {
                                Text(destination.label)
                            },
                            icon = {
                                Icon(destination.icon, destination.contentDescription)
                            },
                            selected = selectedDestination == index,
                            onClick = {
                                navController.navigate(destination.route)
                                selectedDestination = index
                                scope.launch {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController,
            startDestination = startDestination.route,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            val menuButtonHandler: () -> Unit = {
                scope.launch {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
            }
            DrawerDestinations.entries.forEach { destination ->
                composable(destination.route) {
                    when (destination) {
                        DrawerDestinations.Home -> MainPage(
                            modifier,
                            menuButtonHandler,
                            bookView,
                        )

                        DrawerDestinations.ApiSettings -> ApiSettingsPage(
                            modifier,
                            menuButtonHandler
                        )

                        DrawerDestinations.Settings -> {
                            Text("TODO")
                        }
                    }
                }
            }
        }
    }
}
