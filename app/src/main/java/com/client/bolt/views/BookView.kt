package com.client.bolt.views

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.client.bolt.NetworkSingleton
import com.client.bolt.Routes
import com.client.bolt.datastores.FiltersDataStore
import com.client.bolt.getFullUri
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.util.Optional

data class Book(
    val title: String,
    val chapter: Double,
    val coverImage: Optional<String>,
    val id: Int,
    val lastModified: Long,
    val kind: String,
    val onHiatus: Boolean,
    val isFinished: Boolean
)

data class CreateBook(
    val title: String,
    val chapter: Double,
    val coverImage: Optional<String>,
    val kind: String,
    val onHiatus: Boolean,
    val isFinished: Boolean
)

data class PatchBook(
    val title: Optional<String>,
    val chapter: Optional<Double>,
    val coverImage: Optional<String>,
    val id: Int,
    val kind: Optional<String>,
    val onHiatus: Optional<Boolean>,
    val isFinished: Optional<Boolean>
)

enum class JSONBook(
    val value: String
) {
    Title("title"),
    Chapter("chapter"),
    Kind("kind"),
    CoverImage("cover_image"),
    IsFinished("is_finished"),
    OnHiatus("on_hiatus"),

}

enum class BookKinds(
    val string: String
) {
    Book("book"),
    Manga("manga"),
    Manhwa("manhwa"),
    Manhua("manhua"),
}

fun getRequestErrorMessage(data: ByteArray?): String {
    return String(data ?: ByteArray(0), Charsets.UTF_8)
}

fun parseJSON(json: JSONObject): Book {
    return Book(
        title = json.getString("title"),
        chapter = json.getDouble("chapter"),
        coverImage = Optional.empty(), // Unused
        id = json.getInt("id"),
        lastModified = json.getLong("last_modified"),
        kind = json.getString("kind"),
        onHiatus = json.getBoolean("on_hiatus"),
        isFinished = json.getBoolean("is_finished")
    )
}

fun parseJSONArray(jsonArray: JSONArray): List<Book> {
    val list = mutableStateListOf<Book>()
    for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(parseJSON(obj))
    }
    return list.toList()
}

fun jsonFromBook(book: CreateBook): JSONObject {
    val json = JSONObject()
    json.put(JSONBook.Title.value, book.title)
    json.put(JSONBook.Chapter.value, book.chapter)
    json.put(JSONBook.Kind.value, book.kind)
    json.put(
        JSONBook.CoverImage.value,
        book.coverImage.orElseGet { "" }
    )
    json.put(JSONBook.IsFinished.value, book.isFinished)
    json.put(JSONBook.OnHiatus.value, book.onHiatus)
    return json
}

fun jsonFromBook(book: PatchBook): JSONObject {
    val json = JSONObject()
    if (book.title.isPresent) {
        json.put(JSONBook.Title.value, book.title.get())
    }
    if (book.chapter.isPresent) {
        json.put(JSONBook.Chapter.value, book.chapter.get())
    }
    if (book.kind.isPresent) {
        json.put(JSONBook.Kind.value, book.kind.get())
    }
    if (book.coverImage.isPresent) {
        json.put(JSONBook.CoverImage.value, book.coverImage.get())
    }
    if (book.isFinished.isPresent) {
        json.put(JSONBook.IsFinished.value, book.isFinished.get())
    }
    if (book.onHiatus.isPresent) {
        json.put(JSONBook.OnHiatus.value, book.onHiatus.get())
    }
    return json
}

fun getAuthHeaders(username: String?, password: String?): MutableMap<String, String> {
    val credentials = "$username:$password"
    val auth = "Basic " + Base64.encodeToString(
        credentials.toByteArray(),
        Base64.NO_WRAP
    )
    return hashMapOf(
        "Authorization" to auth
    )
}

fun patchBookFromBook(originalBook: Book, editedBook: Book): PatchBook {
    val finalBook = PatchBook(
        title = if (originalBook.title != editedBook.title) {
            Optional.of(editedBook.title)
        } else {
            Optional.empty()
        },
        chapter = if (originalBook.chapter != editedBook.chapter) {
            Optional.of(editedBook.chapter)
        } else {
            Optional.empty()
        },
        coverImage = Optional.empty(),
        id = editedBook.id,
        kind = if (originalBook.kind != editedBook.kind) {
            Optional.of(editedBook.kind)
        } else {
            Optional.empty()
        },
        onHiatus = if (originalBook.onHiatus != editedBook.onHiatus) {
            Optional.of(editedBook.onHiatus)
        } else {
            Optional.empty()
        },
        isFinished = if (originalBook.isFinished != editedBook.isFinished) {
            Optional.of(editedBook.isFinished)
        } else {
            Optional.empty()
        },
    )
    return finalBook
}

class BookView : ViewModel() {
    /**
     * Fetched Book list
     */
    var books by mutableStateOf<List<Book>>(listOf()); private set

    /**
     * Deletes all Books on view list
     */
    fun clearBooks() {
        books = listOf()
    }

    fun reverseBooks() {
        books = books.reversed()
    }

    /**
     * Add fake Book to view list before refetch
     */
    fun addFakeBook(book: Book) {
        books = books + book
    }

    /**
     * Fake Book editing on view list before refetch
     */
    fun fakeEditBook(book: Book) {
        var index = -1;
        books.forEachIndexed({ i, dataBook ->
            if (book.id == dataBook.id) {
                index = i
            }
        })
        if (index == -1) {
            appendLog("ClientError: Book with id '${book.id}' does not exist")
            return
        }

        val copy = books.toMutableList()

        copy.removeAt(index)
        copy.add(book)

        books = copy
    }

    var reverseChecked by mutableStateOf(false)
    var onHiatusChecked by mutableStateOf(true)
    var isFinishedChecked by mutableStateOf(true)
    var showBooksChecked by mutableStateOf(true)
    var showMangaChecked by mutableStateOf(true)
    var showManhwaChecked by mutableStateOf(true)
    var showManhuaChecked by mutableStateOf(true)

    var searchQuery by mutableStateOf("")

    /**
     * Error Log list
     */
    var logList = mutableStateListOf<String>(); private set

    /**
     * Append text Log to view Log list
     */
    fun appendLog(text: String) {
        val date = LocalDate.now()
        val time = LocalTime.now()
        logList.add(
            0,
            "[${
                time.hour.toString().padStart(2, '0')
            }:${
                time.minute.toString().padStart(2, '0')
            }:${
                time.second.toString().padStart(2, '0')
            } - ${
                date.dayOfMonth.toString().padStart(2, '0')
            }/${
                date.monthValue.toString().padStart(2, '0')
            }/${date.year}] LOG: $text"
        )
    }

    suspend fun editBook(
        book: PatchBook,
        context: Context,
        callback: () -> Unit
    ) {
        val network = NetworkSingleton.getInstance(context)

        val url = network.apiSettingsDataStore.url.first()
        val username = network.apiSettingsDataStore.username.first()
        val password = network.apiSettingsDataStore.password.first()

        val req = object : StringRequest(
            Routes.Patch.method,
            getFullUri(url, Routes.Patch, book.id),
            {
                callback()
            },
            {
                if (it.networkResponse == null) {
                    appendLog((it.message ?: it).toString())
                } else {
                    appendLog(getRequestErrorMessage(it.networkResponse.data))
                }
                callback()
            }
        ) {
            override fun getBody(): ByteArray {
                var a = jsonFromBook(book)
                Log.d("DBG", "$a")
                return a.toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> =
                getAuthHeaders(username, password)
        }
        network.queueAdd(req)
    }

    /**
     * Post request to /book route
     */
    suspend fun createBook(
        book: CreateBook,
        context: Context,
        callback: () -> Unit
    ) {
        val network = NetworkSingleton.getInstance(context)

        val url = network.apiSettingsDataStore.url.first()
        val username = network.apiSettingsDataStore.username.first()
        val password = network.apiSettingsDataStore.password.first()

        val body = jsonFromBook(book)

        val req = object : StringRequest(
            Routes.Post.method,
            getFullUri(url, Routes.Post),
            {
                callback()
            },
            {
                if (it.networkResponse == null) {
                    appendLog((it.message ?: it).toString())
                } else {
                    appendLog(getRequestErrorMessage(it.networkResponse.data))
                }
                callback()
            }
        ) {
            override fun getBody(): ByteArray =
                body.toString().toByteArray()

            override fun getHeaders(): MutableMap<String, String> =
                getAuthHeaders(username, password)
        }
        network.queueAdd(req)
    }

    /**
     * Get request to /books route and store them in the view list
     */
    suspend fun fetchAllBooks(
        context: Context,
        callback: () -> Unit = {}
    ) {
        val network = NetworkSingleton.getInstance(context);

        val url = network.apiSettingsDataStore.url.first()
        val username = network.apiSettingsDataStore.username.first()
        val password = network.apiSettingsDataStore.password.first()

        val req = object : JsonArrayRequest(
            Routes.GetAll.method,
            getFullUri(url, Routes.GetAll),
            null,
            { res ->
                books = parseJSONArray(res)
                callback()
            },
            {
                if (it.networkResponse == null) {
                    appendLog((it.message ?: it).toString())
                } else {
                    appendLog(getRequestErrorMessage(it.networkResponse.data))
                }
                callback()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> =
                getAuthHeaders(username, password)
        }
        network.queueAdd(req)
    }
}