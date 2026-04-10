package com.client.bolt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

enum class DropdownItems(

) {

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
            // TODO: disable transitions
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    modifier: Modifier = Modifier,
    menuButtonHandler: () -> Unit,
    bookView: BookView = viewModel(),
) {
    val navController = rememberNavController()
    val startDestination = Destination.BOOKS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var dropdownExpanded by remember { mutableStateOf(false) }
    // TODO: move to BookView?
    var reverseChecked by rememberSaveable { mutableStateOf(false) }
    var onHiatusChecked by rememberSaveable { mutableStateOf(true) }
    var isFinishedChecked by rememberSaveable { mutableStateOf(true) }
    var showBooksChecked by rememberSaveable { mutableStateOf(true) }
    var showMangaChecked by rememberSaveable { mutableStateOf(true) }
    var showManhwaChecked by rememberSaveable { mutableStateOf(true) }
    var showManhuaChecked by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {

                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Filter")
            }
        },
        topBar = {
            Box(
                Modifier
                    .padding(4.dp)
                    .statusBarsPadding(),
            ) {
                val sideButtonsWidth = .15f
                Row(
                    Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = menuButtonHandler,
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(sideButtonsWidth),
                    ) {
                        Icon(
                            Icons.Default.Menu, "Menu",
                            Modifier.fillMaxSize(.6f)
                        )
                    }
                    Box() {
                        IconButton(
                            onClick = {
                                dropdownExpanded = !dropdownExpanded
                            },
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(sideButtonsWidth)
                        ) {
                            Icon(
                                Icons.Default.MoreVert, "Filters",
                                Modifier.fillMaxSize(.6f)
                            )
                        }
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            DropdownItemCheckbox(
                                "Reverse Order",
                                reverseChecked,
                                {
                                    reverseChecked = it
                                }
                            )
                            HorizontalDivider()
                            DropdownItemCheckbox(
                                "Show on Hiatus",
                                onHiatusChecked,
                                {
                                    onHiatusChecked = it
                                }
                            )
                            DropdownItemCheckbox(
                                "Show is Finished",
                                isFinishedChecked,
                                {
                                    isFinishedChecked = it
                                }
                            )
                            HorizontalDivider()
                            DropdownItemCheckbox(
                                "Show Books",
                                showBooksChecked,
                                {
                                    showBooksChecked = it
                                }
                            )
                            DropdownItemCheckbox(
                                "Show Mangas",
                                showMangaChecked,
                                {
                                    showMangaChecked = it
                                }
                            )
                            DropdownItemCheckbox(
                                "Show Manhwas",
                                showManhwaChecked,
                                {
                                    showManhwaChecked = it
                                }
                            )
                            DropdownItemCheckbox(
                                "Show Manhuas",
                                showManhuaChecked,
                                {
                                    showManhuaChecked = it
                                }
                            )
                        }
                    }
                }
                DockedSearchBar(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(1f - sideButtonsWidth * 2),
                    shape = RoundedCornerShape(10.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            modifier = Modifier.fillMaxWidth(),
                            query = query,
                            onQueryChange = { query = it },
                            expanded = expanded,
                            onSearch = { expanded = false },
                            onExpandedChange = {
                                expanded = it
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, "Search")
                            },
                            placeholder = {
                                Text("Search")
                            }
                        )
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    Text("TODO: scrollable book list, by name")
                }
            }
        },
        bottomBar = {
            BottomBar(
                selectedDestination = selectedDestination,
                onClick = { destination: Destination, index: Int ->
                    navController.navigate(destination.route)
                    selectedDestination = index
                },
            )
        }
    ) { paddingValues ->
        AppNavHost(
            navController,
            startDestination,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            bookView,
        )
    }
}