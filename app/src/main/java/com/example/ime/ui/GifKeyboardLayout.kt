package com.example.ime.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GifItem(
    val id: String,
    val title: String,
    val category: String,
    val previewEmoji: String,
    val mediaUrl: String,
    val gradientColors: List<Color>
)

object GifData {
    val CATEGORIES = listOf("Trending", "Reactions", "Cheers", "Gaming", "Love", "Laugh", "Memes", "Work")

    val ITEMS = listOf(
        GifItem("g1", "Mind Blown 🤯", "Reactions", "🤯", "https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy.gif", listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))),
        GifItem("g2", "Party Dance 🎉", "Cheers", "🎉", "https://media.giphy.com/media/l0MYt5jPR6QX5pnqM/giphy.gif", listOf(Color(0xFFEC4899), Color(0xFFF43F5E))),
        GifItem("g3", "Victory GG 🏆", "Gaming", "🎮", "https://media.giphy.com/media/artj92V8o75VPL7AeQ/giphy.gif", listOf(Color(0xFF10B981), Color(0xFF059669))),
        GifItem("g4", "Much Love ❤️", "Love", "❤️", "https://media.giphy.com/media/26FLdmIp6wJr91JAI/giphy.gif", listOf(Color(0xFFE11D48), Color(0xFFFB7185))),
        GifItem("g5", "ROFL Laugh 😂", "Laugh", "😂", "https://media.giphy.com/media/10JhviFuU2gWD6/giphy.gif", listOf(Color(0xFFF59E0B), Color(0xFFD97706))),
        GifItem("g6", "Thumbs Up 👍", "Reactions", "👍", "https://media.giphy.com/media/111ebonMs90YLu/giphy.gif", listOf(Color(0xFF6366F1), Color(0xFF4F46E5))),
        GifItem("g7", "Excited Yay 🙌", "Cheers", "🙌", "https://media.giphy.com/media/5GoVLqeAOo6PK/giphy.gif", listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))),
        GifItem("g8", "Coding Fast 💻", "Work", "💻", "https://media.giphy.com/media/unQ3IJU2RG7DO/giphy.gif", listOf(Color(0xFF0284C7), Color(0xFF0EA5E9))),
        GifItem("g9", "Popcorn Meme 🍿", "Memes", "🍿", "https://media.giphy.com/media/gl0mkIZOW6Nwc/giphy.gif", listOf(Color(0xFFEAB308), Color(0xFFCA8A04))),
        GifItem("g10", "Let's Go Rocket 🚀", "Trending", "🚀", "https://media.giphy.com/media/mi6sub6zkR528/giphy.gif", listOf(Color(0xFF6366F1), Color(0xFF06B6D4))),
        GifItem("g11", "Clapping Hands 👏", "Reactions", "👏", "https://media.giphy.com/media/3o72FcJmLzIdYJdmDe/giphy.gif", listOf(Color(0xFF10B981), Color(0xFF3B82F6))),
        GifItem("g12", "Coffee Recharge ☕", "Work", "☕", "https://media.giphy.com/media/3oriO04qxVReM5rJEA/giphy.gif", listOf(Color(0xFFB45309), Color(0xFF78350F))),
        GifItem("g13", "Level Up ⚡", "Gaming", "⚡", "https://media.giphy.com/media/3oKIPnAiaMCws8nOsE/giphy.gif", listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))),
        GifItem("g14", "Confetti Winner 🎊", "Cheers", "🎊", "https://media.giphy.com/media/26tOZ42Mg6pbTUPHW/giphy.gif", listOf(Color(0xFFEC4899), Color(0xFFF59E0B))),
        GifItem("g15", "Cat Vibe 🐱", "Memes", "🐱", "https://media.giphy.com/media/jpbnoe3UIa8TU8LM13/giphy.gif", listOf(Color(0xFF14B8A6), Color(0xFF0D9488))),
        GifItem("g16", "Heart Eyes 😍", "Love", "😍", "https://media.giphy.com/media/3o7TKoWXm3okO1kgHC/giphy.gif", listOf(Color(0xFFF43F5E), Color(0xFFE11D48)))
    )
}

@Composable
fun GifKeyboardLayout(
    themePalette: ImeThemePalette,
    searchQuery: String = "",
    onQueryChange: (String) -> Unit = {},
    onSelectGif: (GifItem) -> Unit,
    onBackToAlpha: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Trending") }

    val filteredGifs = remember(selectedCategory, searchQuery) {
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            GifData.ITEMS.filter { it.title.lowercase().contains(q) || it.category.lowercase().contains(q) }
        } else if (selectedCategory == "Trending") {
            GifData.ITEMS
        } else {
            GifData.ITEMS.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // TOP SEARCH BAR + BACK ACTION
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(themePalette.keyBackgroundPressed)
                    .clickable { onBackToAlpha() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = themePalette.keyText,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(themePalette.keyBackground)
                    .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search GIFs",
                    tint = themePalette.accentPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (searchQuery.isEmpty()) "Type on keyboard to search GIFs..." else searchQuery,
                    style = TextStyle(
                        color = if (searchQuery.isEmpty()) themePalette.keySubtext.copy(alpha = 0.6f) else themePalette.keyText,
                        fontSize = 11.5.sp,
                        fontWeight = if (searchQuery.isEmpty()) FontWeight.Normal else FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = themePalette.keySubtext,
                        modifier = Modifier
                            .size(15.dp)
                            .clickable { onQueryChange("") }
                    )
                }
            }
        }

        // CATEGORY CHIPS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GifData.CATEGORIES.forEach { category ->
                val isSelected = selectedCategory == category && searchQuery.isEmpty()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) themePalette.accentPrimary else themePalette.keyBackgroundPressed)
                        .border(
                            0.5.dp,
                            if (isSelected) themePalette.accentPrimary else themePalette.border.copy(alpha = 0.3f),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            selectedCategory = category
                            if (searchQuery.isNotEmpty()) {
                                onQueryChange("")
                            }
                        }
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = category,
                        style = TextStyle(
                            color = if (isSelected) Color.White else themePalette.keySubtext,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        // GIF GRID RESULTS
        if (filteredGifs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No GIFs found for \"$searchQuery\". Try another word!",
                    style = TextStyle(color = themePalette.keySubtext, fontSize = 11.sp)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredGifs, key = { it.id }) { gif ->
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(gif.gradientColors))
                            .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable { onSelectGif(gif) }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = gif.previewEmoji,
                                fontSize = 18.sp
                            )
                            Text(
                                text = gif.title,
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
