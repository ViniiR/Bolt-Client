package com.client.bolt.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.client.bolt.AppNavHost
import com.client.bolt.BottomBar
import com.client.bolt.Destination
import com.client.bolt.Kinds
import com.client.bolt.components.DropdownItemCheckbox
import com.client.bolt.ui.theme.AppBorderShapes
import com.client.bolt.ui.theme.AppIcons
import com.client.bolt.views.Book
import com.client.bolt.views.BookView
import com.client.bolt.views.CreateBook
import com.client.bolt.views.PatchBook
import com.client.bolt.views.patchBookFromBook
import kotlinx.coroutines.launch
import java.util.Optional

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    modifier: Modifier = Modifier,
    menuButtonHandler: () -> Unit,
    bookView: BookView,
) {
    val navController = rememberNavController()
    val startDestination = Destination.BOOKS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var reverseChecked by rememberSaveable { mutableStateOf(false) }
    var onHiatusChecked by rememberSaveable { mutableStateOf(true) }
    var isFinishedChecked by rememberSaveable { mutableStateOf(true) }
    var showBooksChecked by rememberSaveable { mutableStateOf(true) }
    var showMangaChecked by rememberSaveable { mutableStateOf(true) }
    var showManhwaChecked by rememberSaveable { mutableStateOf(true) }
    var showManhuaChecked by rememberSaveable { mutableStateOf(true) }

    var showBottomSheet by remember { mutableStateOf(false) }
    var editBook by remember { mutableStateOf<Book?>(null) }

    fun setBottomSheet(value: Boolean) {
        showBottomSheet = value
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    setBottomSheet(true)
                },
                shape = AppBorderShapes.rounded,
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
                    shape = AppBorderShapes.roundedSquare,
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
        if (showBottomSheet) {
            BookSheet({
                setBottomSheet(false)
                editBook = null
            }, bookView, editBook)
        }
        AppNavHost(
            navController,
            startDestination,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            bookView,
            showBottomSheet,
            { value: Boolean, book: Book? ->
                setBottomSheet(value)
                editBook = book
            }
        )
    }
}

enum class AddBookField {
    Title,
    Chapter
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookSheet(onDismiss: () -> Unit, bookView: BookView, book: Book?) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var chapter by remember { mutableStateOf("") }
    var finished by remember { mutableStateOf(false) }
    var hiatus by remember { mutableStateOf(false) }
    var selectedKind by remember { mutableStateOf(Kinds.Book) }

    if (book != null) {
        title = book.title
        chapter = book.chapter.toString()
        finished = book.isFinished
        hiatus = book.onHiatus
        selectedKind = when (book.kind) {
            "manga" -> Kinds.Manga
            "manhwa" -> Kinds.Manhwa
            "manhua" -> Kinds.Manhua
            "book" -> Kinds.Book
            else -> {
                bookView.appendLog("Edit Book kind is unknown: ${book.kind}")
                Kinds.Book
            }
        }
    }

    var error by remember { mutableStateOf<Pair<AddBookField, String>?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Row(
            Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                when (book) {
                    is Book -> "Edit Book"
                    null -> "Add Book"
                },
                fontSize = 25.sp,
            )
        }
        error?.let {
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(it.second, color = MaterialTheme.colorScheme.error)
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column() {
                OutlinedTextField(
                    placeholder = {
                        Text("Tales of Demons and Gods")
                    },
                    label = {
                        Text("Title")
                    },
                    isError = error?.first == AddBookField.Title,
                    value = title,
                    onValueChange = {
                        if (error?.first == AddBookField.Title) {
                            error = null
                        }
                        title = it
                    },
                    modifier = Modifier.fillMaxWidth(1f)
                )
                OutlinedTextField(
                    placeholder = {
                        Text("90")
                    },
                    label = {
                        Text("Chapter")
                    },
                    value = chapter,
                    isError = error?.first == AddBookField.Chapter,
                    onValueChange = {
                        val number = it.trim().toDoubleOrNull()
                        if (number == null && it.isNotEmpty()) {
                            error = Pair(AddBookField.Chapter, "Chapter is not a valid number")
                            chapter = it
                            return@OutlinedTextField
                        }
                        error = null
                        chapter = it
                    },
                    modifier = Modifier.fillMaxWidth(1f)
                )
            }
            SingleChoiceSegmentedButtonRow() {
                Kinds.entries.forEach {
                    SegmentedButton(
                        label = {
                            Text(
                                it.value.replaceFirstChar { it.uppercase() }
                            )
                        },
                        selected = selectedKind == it,
                        onClick = {
                            selectedKind = it
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = it.ordinal,
                            count = Kinds.entries.size
                        )
                    )
                }

            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    Modifier
                        .clickable {
                            finished = !finished
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Checkbox(
                        onCheckedChange = null,
                        checked = finished,
                    )
                    Text("Finished")
                }
                Row(
                    Modifier
                        .clickable {
                            hiatus = !hiatus
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Checkbox(
                        onCheckedChange = null,
                        checked = hiatus,
                    )
                    Text("Hiatus")
                }
            }
            Row(
                Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    enabled = if (book != null) {
                        (book.title != title ||
                                book.chapter.toString() != chapter ||
                                book.kind != selectedKind.value ||
                                book.isFinished != finished ||
                                book.onHiatus != hiatus) &&
                                (!title.isEmpty() && !chapter.isEmpty() && error == null)
                    } else {
                        (!title.isEmpty() && !chapter.isEmpty() && error == null)
                    },
                    shape = AppBorderShapes.roundedSquare,
                    onClick = {
                        if (title.isEmpty()) {
                            error = Pair(AddBookField.Title, "Title cannot be empty")
                            return@Button
                        }
                        val number = chapter.trim().toDoubleOrNull()
                        if (number == null) {
                            error = Pair(AddBookField.Chapter, "Chapter is not a valid number")
                            return@Button
                        }
                        scope.launch {
                            if (book == null) {
                                bookView.createBook(
                                    CreateBook(
                                        title,
                                        chapter.toDoubleOrNull()!!,
                                        coverImage = Optional.empty(),
                                        kind = selectedKind.value,
                                        onHiatus = hiatus,
                                        isFinished = finished
                                    ),
                                    context,
                                    {
                                        bookView.addFakeBook(
                                            Book(
                                                title,
                                                chapter.toDoubleOrNull()!!,
                                                coverImage = Optional.empty(),
                                                id = 0,
                                                lastModified = System.currentTimeMillis() / 1000L,
                                                kind = selectedKind.value,
                                                onHiatus = hiatus,
                                                isFinished = finished
                                            )
                                        )
                                        onDismiss()
                                    }
                                )
                            } else {
                                bookView.editBook(
                                    patchBookFromBook(
                                        book,
                                        Book(
                                            title,
                                            chapter.toDouble(),
                                            Optional.empty(),
                                            book.id,
                                            book.lastModified,
                                            selectedKind.value,
                                            finished,
                                            hiatus
                                        )
                                    ),
                                    context,
                                    {
                                        bookView.fakeEditBook(book)
                                        onDismiss()
                                    }
                                )
                            }
                        }
                    }
                ) {
                    Icon(AppIcons.Submit.value, "Submit")
                    Spacer(Modifier.width(10.dp))
                    Text("Submit")
                }
            }
        }
    }
}