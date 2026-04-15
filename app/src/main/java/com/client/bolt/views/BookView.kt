package com.client.bolt.views

import android.content.Context
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.client.bolt.NetworkSingleton
import com.client.bolt.Routes
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

class BookView : ViewModel() {
    var books by mutableStateOf<List<Book>>(listOf()); private set

    fun clearBooks() {
        books = listOf()
    }

    fun addFakeBook(book: Book) {
        books = books + book
    }

//    var reverseChecked by rememberSaveable { mutableStateOf(false) }
//    var onHiatusChecked by rememberSaveable { mutableStateOf(true) }
//    var isFinishedChecked by rememberSaveable { mutableStateOf(true) }
//    var showBooksChecked by rememberSaveable { mutableStateOf(true) }
//    var showMangaChecked by rememberSaveable { mutableStateOf(true) }
//    var showManhwaChecked by rememberSaveable { mutableStateOf(true) }
//    var showManhuaChecked by rememberSaveable { mutableStateOf(true) }

    var logList = mutableStateListOf<String>(); private set

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

    // TODO: unify network requets to avoid copy paste
    suspend fun createBook(
        book: CreateBook,
        context: Context,
        callback: () -> Unit
    ) {
        val network = NetworkSingleton.getInstance(context)

        val url = network.apiSettingsDataStore.url.first()
        val username = network.apiSettingsDataStore.username.first()
        val password = network.apiSettingsDataStore.password.first()

        val body = JSONObject()
        body.put(JSONBook.Title.value, book.title)
        body.put(JSONBook.Chapter.value, book.chapter)
        body.put(JSONBook.Kind.value, book.kind)
        body.put(
            JSONBook.CoverImage.value,
            book.coverImage.orElseGet { "" }
        )
        body.put(JSONBook.IsFinished.value, book.isFinished)
        body.put(JSONBook.OnHiatus.value, book.onHiatus)

        val req = object : StringRequest(
            Routes.Post.method,
            getFullUri(url, Routes.Post),
            {
                callback()
            },
            {
                appendLog(
                    if (it.networkResponse == null) {
                        (it.message ?: it).toString()
                    } else {
                        getRequestErrorMessage(it.networkResponse.data)
                    }
                )
                callback()
            }
        ) {
            override fun getBody(): ByteArray {
                return body.toString().toByteArray()
            }
            override fun getHeaders(): MutableMap<String, String> {
                val credentials = "$username:$password"
                val auth = "Basic " + Base64.encodeToString(
                    credentials.toByteArray(),
                    Base64.NO_WRAP
                )
                return hashMapOf(
                    "Authorization" to auth
                )
            }
        }
        network.queueAdd(req)
    }

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
            { err ->
                appendLog(
                    if (err.networkResponse == null) {
                        (err.message ?: err).toString()
                    } else {
                        getRequestErrorMessage(err.networkResponse.data)
                    }
                )
                callback()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val credentials = "$username:$password"
                val auth = "Basic " + Base64.encodeToString(
                    credentials.toByteArray(),
                    Base64.NO_WRAP
                )
                return hashMapOf(
                    "Authorization" to auth
                )
            }
        }
        network.queueAdd(req)
    }
}