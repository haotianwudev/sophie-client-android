package com.example.sophieaianalyst.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sophieaianalyst.ui.theme.SophieTheme

/**
 * BookmarkButton component
 */
@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint by animateColorAsState(
        targetValue = if (isBookmarked) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "bookmark_color"
    )
    
    IconButton(
        onClick = onToggleBookmark,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
            contentDescription = if (isBookmarked) "Remove from bookmarks" else "Add to bookmarks",
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview
@Composable
fun BookmarkButtonPreview() {
    SophieTheme {
        BookmarkButton(
            isBookmarked = false,
            onToggleBookmark = {}
        )
    }
}

@Preview
@Composable
fun BookmarkButtonBookmarkedPreview() {
    SophieTheme {
        BookmarkButton(
            isBookmarked = true,
            onToggleBookmark = {}
        )
    }
} 