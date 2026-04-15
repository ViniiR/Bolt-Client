package com.client.bolt.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.client.bolt.Kinds
import com.client.bolt.ui.theme.AppBorderShapes
import com.client.bolt.ui.theme.BookColors
import com.client.bolt.ui.theme.BookWrapperColors
import com.client.bolt.views.Book

fun getRelativeTime(time: Long, now: Long = System.currentTimeMillis()): String {
    val diffMillis = now - time
    val diffMinutes = diffMillis / (1000 * 60)
    val diffHours = diffMillis / (1000 * 60 * 60)
    val diffDays = diffMillis / (1000 * 60 * 60 * 24)
    val diffWeeks = diffDays / 7
    val diffMonths = diffDays / 30

    return when {
        diffMinutes < 60 -> "${diffMinutes}min ago"
        diffHours < 24 -> "${diffHours}h ago"
        diffDays < 7 -> "${diffDays}d ago"
        diffDays < 30 -> "${diffWeeks}w ago"
        else -> "${diffMonths}mo ago"
    }
}

@Composable
private fun Wrapper(book: Book) {
    val color = if (book.isFinished) {
        BookWrapperColors.finished
    } else if (book.onHiatus) {
        BookWrapperColors.hiatus
    } else {
        Color.White
    }
    val text = if (book.isFinished) {
        "FINISHED"
    } else if (book.onHiatus) {
        "HIATUS"
    } else {
        return
    }

    Box(
        Modifier
            .zIndex(1f)
            .rotate(-25f)
            .height(30.dp)
            .requiredWidth(250.dp)
            .background(color = color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            letterSpacing = 6.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BookNode(
    book: Book
) {
    val relative = getRelativeTime(book.lastModified * 1000L)

    val chapter = if ((book.chapter % 1.0) == 0.0) {
        book.chapter.toInt()
    } else {
        book.chapter
    }


    Box(
        Modifier
            .background(
                color = Color.DarkGray, shape = AppBorderShapes.roundedSubtle
            )
            .clip(AppBorderShapes.roundedSubtle),
        contentAlignment = Alignment.Center
    ) {
        Wrapper(book)
        Column {
            Column(
                Modifier.padding(6.dp)
            ) {
                Text(
                    book.title,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    softWrap = false
                )
                HorizontalDivider(Modifier.height(10.dp))
                Text("Ch. $chapter")
            }
            Row(
                Modifier
                    .background(
                        color = when (book.kind) {
                            Kinds.Book.value -> BookColors.Book
                            Kinds.Manga.value -> BookColors.Manga
                            Kinds.Manhwa.value -> BookColors.Manhwa
                            Kinds.Manhua.value -> BookColors.Manhua
                            else -> Color.Red
                        }
                    )
                    .fillMaxWidth(1f)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val color = if (book.kind == Kinds.Manga.value) {
                    Color.Black
                } else {
                    Color.White
                }
                Text(
                    book.kind.replaceFirstChar { it.uppercase() },
                    color = color
                )
                Text(
                    relative,
                    color = color
                )
            }
        }
    }
}