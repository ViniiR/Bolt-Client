package com.client.bolt.pages

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.client.bolt.AppNavHost
import com.client.bolt.BottomBar
import com.client.bolt.Destination
import com.client.bolt.Kinds
import com.client.bolt.components.DropdownItemCheckbox
import com.client.bolt.datastores.FiltersDataStore
import com.client.bolt.ui.theme.AppBorderShapes
import com.client.bolt.ui.theme.AppIcons
import com.client.bolt.ui.theme.RedButton
import com.client.bolt.views.Book
import com.client.bolt.views.BookView
import com.client.bolt.views.CreateBook
import com.client.bolt.views.patchBookFromBook
import kotlinx.coroutines.launch
import java.util.Optional

data class MainPageActions(
    val setSelectionMode: (Boolean) -> Unit,
    val getSelectionMode: () -> Boolean,
    val updateList: (Int) -> Unit,
    val getList: () -> List<Int>,
    val clearList: () -> Unit,
    val setBottomSheet: (Boolean, Book?) -> Unit,
    val getIsBottomSheetVisible: () -> Boolean
)

val LocalMainPageActionHandler = staticCompositionLocalOf {
    MainPageActions(
        setSelectionMode = {
            error("No Handler defined")
        },
        getSelectionMode = {
            error("No Handler defined")
        },
        updateList = {
            error("No Handler defined")
        },
        getList = {
            error("No Handler defined")
        },
        clearList = {
            error("No Handler defined")
        },
        setBottomSheet = { _, _ ->
            error("No Handler defined")
        },
        getIsBottomSheetVisible = {
            error("No Handler defined")
        }
    )
}

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

    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val filtersDataStore = remember { FiltersDataStore(context) }

    // TODO: use remember keys
    val storedReverse by filtersDataStore.reverse.collectAsStateWithLifecycle(null)
    val storedFinished by filtersDataStore.fininshed.collectAsStateWithLifecycle(null)
    val storedHiatus by filtersDataStore.hiatus.collectAsStateWithLifecycle(null)
    val storedBooks by filtersDataStore.books.collectAsStateWithLifecycle(null)
    val storedManga by filtersDataStore.manga.collectAsStateWithLifecycle(null)
    val storedManhwa by filtersDataStore.manhwa.collectAsStateWithLifecycle(null)
    val storedManhua by filtersDataStore.manhua.collectAsStateWithLifecycle(null)

    // TODO: bad?
    LaunchedEffect(storedReverse) {
        if (storedReverse != null) {
            bookView.reverseChecked = storedReverse ?: true
        }
        if (storedFinished != null) {
            bookView.isFinishedChecked = storedFinished ?: true
        }
        if (storedHiatus != null) {
            bookView.onHiatusChecked = storedHiatus ?: true
        }
        if (storedBooks != null) {
            bookView.showBooksChecked = storedBooks ?: true
        }
        if (storedManga != null) {
            bookView.showMangaChecked = storedManga ?: true
        }
        if (storedManhwa != null) {
            bookView.showManhwaChecked = storedManhwa ?: true
        }
        if (storedManhua != null) {
            bookView.showManhuaChecked = storedManhua ?: true
        }
    }


    var isSelectionModeActive by remember { mutableStateOf(false) }
    val selectionList = remember { mutableStateListOf<Int>() }

    fun setSelectionMode(value: Boolean) {
        isSelectionModeActive = value
    }


    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var dropdownExpanded by remember { mutableStateOf(false) }

    var editBook by remember { mutableStateOf<Book?>(null) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showAlertDialogue by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalMainPageActionHandler provides MainPageActions(
            {
                setSelectionMode(it)
                if (!it) {
                    selectionList.clear()
                }
            }, {
                isSelectionModeActive
            }, {
                if (selectionList.contains(it)) {
                    selectionList.removeIf { item ->
                        item == it
                    }
                } else {
                    selectionList.add(it)
                }
            }, {
                selectionList.toList()
            }, {
                selectionList.clear()
                isSelectionModeActive = false
            },
            { visible, book ->
                showBottomSheet = visible
                editBook = book
            }, {
                showBottomSheet
            }
        )
    ) {
        Scaffold(
            modifier = modifier,
            floatingActionButton = fab@{
                if (isSelectionModeActive) {
                    return@fab
                }
                ExtendedFloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    },
                    shape = AppBorderShapes.rounded,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Filter")
                }
            },
            topBar = topBar@{
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
                        if (isSelectionModeActive) {
                            Row(
                                Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    "Selected ${selectionList.size} Items",
                                    fontSize = 20.sp
                                )
                                IconButton(
                                    onClick = {
                                        if (selectionList.size == bookView.books.size) {
                                            selectionList.clear()
                                            return@IconButton
                                        }

                                        bookView.books.forEach {
                                            if (!selectionList.contains(it.id)) {
                                                selectionList.add(it.id)
                                            }
                                        }
                                    }
                                ) {
                                    // TODO: change icon
                                    val icon = if (selectionList.size == bookView.books.size) {
                                        Icons.Default.CheckCircle
                                    } else {
                                        Icons.Outlined.CheckCircle
                                    }
                                    Icon(
                                        icon, "Select all books"
                                    )
                                }
                            }
                            return@topBar
                        }

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
                                fun Modifier.text(): Modifier {
                                    return Modifier.padding(12.dp, 12.dp, 6.dp, 6.dp)
                                }

                                Text("Ordering", Modifier.text())
                                DropdownItemCheckbox(
                                    "Reverse Order",
                                    bookView.reverseChecked,
                                    {
                                        bookView.reverseChecked = it
                                        scope.launch {
                                            filtersDataStore.updateReverse(it)
                                        }
                                    }
                                )
                                HorizontalDivider()
                                Text("Releasing Status", Modifier.text())
                                DropdownItemCheckbox(
                                    "Show on Hiatus",
                                    bookView.onHiatusChecked,
                                    {
                                        bookView.onHiatusChecked = it
                                        scope.launch {
                                            filtersDataStore.updateHiatus(it)
                                        }
                                    }
                                )
                                DropdownItemCheckbox(
                                    "Show is Finished",
                                    bookView.isFinishedChecked,
                                    {
                                        bookView.isFinishedChecked = it
                                        scope.launch {
                                            filtersDataStore.updateFinished(it)
                                        }
                                    }
                                )
                                HorizontalDivider()
                                Text("Filters", Modifier.text())
                                DropdownItemCheckbox(
                                    "Show Books",
                                    bookView.showBooksChecked,
                                    {
                                        bookView.showBooksChecked = it
                                        scope.launch {
                                            filtersDataStore.updateBooks(it)
                                        }
                                    }
                                )
                                DropdownItemCheckbox(
                                    "Show Mangas",
                                    bookView.showMangaChecked,
                                    {
                                        bookView.showMangaChecked = it
                                        scope.launch {
                                            filtersDataStore.updateManga(it)
                                        }
                                    }
                                )
                                DropdownItemCheckbox(
                                    "Show Manhwas",
                                    bookView.showManhwaChecked,
                                    {
                                        bookView.showManhwaChecked = it
                                        scope.launch {
                                            filtersDataStore.updateManhwa(it)
                                        }
                                    }
                                )
                                DropdownItemCheckbox(
                                    "Show Manhuas",
                                    bookView.showManhuaChecked,
                                    {
                                        bookView.showManhuaChecked = it
                                        scope.launch {
                                            filtersDataStore.updateManhua(it)
                                        }
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
            bottomBar = bottomBar@{
                if (isSelectionModeActive) {
                    Row(
                        Modifier
                            .height(80.dp)
                            .fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            enabled = selectionList.isNotEmpty(),
                            onClick = {
                                showAlertDialogue = true
                            }
                        ) {
                            Icon(Icons.Default.Delete, "Delete all selected books")
                            Text("Delete")
                        }
                    }
                    return@bottomBar
                }

                BottomBar(
                    selectedDestination = selectedDestination,
                    onClick = { destination: Destination, index: Int ->
                        navController.navigate(destination.route)
                        selectedDestination = index
                    },
                )
            }
        ) { paddingValues ->
            if (isSelectionModeActive) {
                BackHandler(true) {
                    isSelectionModeActive = false
                    selectionList.clear()
                }
            }

            if (showBottomSheet) {
                BookSheet(bottomSheetState, {
                    showBottomSheet = false
                    editBook = null
                }, bookView, editBook)
            }

            if (showAlertDialogue) {
                AlertDialog(
                    onDismissRequest = {
                        showAlertDialogue = false
                    },
                    confirmButton = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = { showAlertDialogue = false }
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                modifier = Modifier
                                    .weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = RedButton.ContainerBackground),
                                onClick = {
                                    scope.launch {
                                        if (selectionList.size == 1) {
                                            bookView.deleteBook(
                                                context,
                                                selectionList[0],
                                                {
                                                    showAlertDialogue = false
                                                    isSelectionModeActive = false
                                                    bookView.fakeDeleteBook(selectionList[0])
                                                    selectionList.clear()
                                                }
                                            )
                                        } else if (selectionList.size > 1) {
                                            bookView.deleteBook(
                                                context,
                                                selectionList.toList(),
                                                {
                                                    showAlertDialogue = false
                                                    isSelectionModeActive = false
                                                    bookView.fakeDeleteBook(selectionList.toList())
                                                    selectionList.clear()
                                                }
                                            )
                                        }
                                    }
                                }
                            ) {
                                Text("Delete", color = RedButton.Text)
                            }
                        }
                    },
                    dismissButton = null,
                    title = {
                        val text = if (selectionList.size == 1) {
                            "Delete this book?"
                        } else {
                            "Delete ${selectionList.size} books?"
                        }
                        Text(text)
                    },
                    icon = {
                        Icon(Icons.Default.Delete, "Delete books")
                    }
                )
            }
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
}

enum class AddBookField {
    Title,
    Chapter
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookSheet(state: SheetState, onDismiss: () -> Unit, bookView: BookView, book: Book?) {
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
        sheetState = state,
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
                                                id = it
                                                    ?: 0, // Should never really be 0 but just to avoid crashes
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
                                val editedBook = Book(
                                    title,
                                    chapter.toDouble(),
                                    Optional.empty(),
                                    book.id,
                                    System.currentTimeMillis() / 1000L,
                                    selectedKind.value,
                                    finished,
                                    hiatus
                                )
                                bookView.editBook(
                                    patchBookFromBook(
                                        book,
                                        editedBook
                                    ),
                                    context,
                                    {
                                        bookView.fakeEditBook(editedBook)
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