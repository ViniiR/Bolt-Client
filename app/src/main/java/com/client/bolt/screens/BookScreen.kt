package com.client.bolt.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.client.bolt.views.BookView
import kotlinx.coroutines.launch

//https://google.github.io/volley/request.html
@Composable
private fun Skeleton() {
    fun Modifier.skeletonBar(maxWidth: Float) =
        this
            .background(color = Color.Gray, shape = RoundedCornerShape(1.dp))
            .padding(1.dp)
            .fillMaxWidth(maxWidth)

    val book: @Composable RowScope.() -> Unit = {
        Box(
            modifier = Modifier
                .background(
                    color = Color.Transparent, shape = RoundedCornerShape(2.dp)
                )
                .fillMaxWidth(1.0f)
                .fillMaxHeight(1.0f)
                .padding(5.dp)
                .weight(1.0f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    Modifier
                        .skeletonBar(.7f)
                        .weight(1f)
                )
                Box(
                    Modifier
                        .skeletonBar(.5f)
                        .weight(1f)
                )
                Box(
                    Modifier
                        .skeletonBar(.6f)
                        .weight(1f)
                )
            }
        };
    }
    val row: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth(1.0f)
                .height(100.dp),
        ) {
            for (_i in 0..1) {
                book()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (_i in 0..6) {
            row()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
// FIXME: shows skeleton when reloading page, save data
fun BooksScreen(viewModel: BookView = viewModel()) {
    val data = viewModel.books
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }

    var fetchData by rememberSaveable() { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.fetchAllBooks(context)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            scope.launch {
                viewModel.clearBooks()
                viewModel.fetchAllBooks(context, {
                    isRefreshing = false
                })
            }
        },
        Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            Modifier.fillMaxSize(),
        ) {
            items(1) {
                if (viewModel.logList.isNotEmpty() && data == null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Check the Logs!")
                    }
                    return@items
                }
                if (data == null) {
                    // TODO
                    // when this line is removed it displays check logs even after refresh
//                    fetchData = !fetchData
                    Skeleton()
                    return@items
                }
                ListItem({
                    Text(data.toString())
                })
            }
        }
    }
}
