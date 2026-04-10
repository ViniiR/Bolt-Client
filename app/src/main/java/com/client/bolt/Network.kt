package com.client.bolt

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.client.bolt.datastores.ApiSettingsDataStore
import kotlin.system.exitProcess

enum class Routes(
    val string: String,
    val method: Int,
) {
    GetAll("books", Request.Method.GET),
    Get("book", Request.Method.GET),
    Post("book", Request.Method.POST),
    Patch("book", Request.Method.PATCH),
    Delete("book", Request.Method.DELETE),
}

fun getFullUri(url: String?, route: Routes): String {
    if (url.isNullOrBlank()) {
        return ""
    }
    return String.format("$url/${route.string}")
}

fun getFullUri(url: String?, route: Routes, id: Int): String {
    if (url.isNullOrBlank()) {
        return ""
    }
    return String.format("$url/${route.string}/$id")
}

class NetworkSingleton private constructor(context: Context) {
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context.applicationContext)

    fun <T> queueAdd(request: Request<T>) {
        requestQueue.add(request)
    }

    val apiSettingsDataStore = ApiSettingsDataStore(context)

    companion object {
        @Volatile
        private var INSTANCE: NetworkSingleton? = null

        fun getInstance(context: Context): NetworkSingleton {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkSingleton(context).also {
                    INSTANCE = it
                }
            }
        }

    }
}