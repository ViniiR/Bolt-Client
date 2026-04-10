package com.client.bolt

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.client.bolt.screens.BooksScreen
import com.client.bolt.screens.LogScreen
import com.client.bolt.views.BookView

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    BOOKS(
        "books",
        "Books",
        Icons.AutoMirrored.Default.ExitToApp,
        "Books"
    ),
    LOGS("logs", "Logs", Icons.Default.Info, "Logs")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(menuHandler: () -> Unit, title: String = "Bolt") {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(
                onClick = menuHandler
            ) {
                Icon(Icons.Default.Menu, "Menu")
            }
        },
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    bookView: BookView = viewModel(),
) {
    NavHost(
        navController,
        modifier = modifier,
        startDestination = startDestination.route,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        }
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.BOOKS -> BooksScreen(bookView)
                    Destination.LOGS -> LogScreen(bookView)
                }
            }
        }
    }

}


@Composable
fun BottomBar(
    selectedDestination: Int,
    onClick: (Destination, Int) -> Unit
) {
    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestination == index,
                onClick = {
                    onClick(destination, index)
                },
                icon = {
                    Icon(
                        destination.icon,
                        contentDescription = destination.contentDescription
                    )
                },
                label = {
                    Text(destination.label)
                }
            )
        }
    }
}
