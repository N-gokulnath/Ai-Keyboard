package com.example.ime.ui

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class StickerItem(
    val id: String,
    val name: String,
    val packName: String,
    val emoji: String,
    val badge: String,
    val imageUrl: String = "",
    val backgroundGradient: List<Color>
)

object StickerData {
    val PACKS = listOf("All", "Aura Cats", "Moods & Vibes", "Tech Life", "Cyber Future", "Expressions")

    val ITEMS = listOf(
        StickerItem("s1", "Cool Cat", "Aura Cats", "😺🕶️", "STUNNING", "https://api.dicebear.com/7.x/bottts/png?seed=CoolCat", listOf(Color(0xFF6366F1), Color(0xFF4F46E5))),
        StickerItem("s2", "Space Rocket", "Tech Life", "🚀✨", "HYPER", "https://api.dicebear.com/7.x/bottts/png?seed=Rocket", listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))),
        StickerItem("s3", "Fire Aura", "Moods & Vibes", "🔥💎", "HOT", "https://api.dicebear.com/7.x/bottts/png?seed=FireAura", listOf(Color(0xFFF97316), Color(0xFFEF4444))),
        StickerItem("s4", "Party Time", "Expressions", "🥳🎉", "EPIC", "https://api.dicebear.com/7.x/bottts/png?seed=PartyTime", listOf(Color(0xFFEAB308), Color(0xFFF59E0B))),
        StickerItem("s5", "Robot Friend", "Cyber Future", "🤖⚡", "CYBER", "https://api.dicebear.com/7.x/bottts/png?seed=RobotFriend", listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))),
        StickerItem("s6", "Heart Glow", "Moods & Vibes", "💖✨", "SWEET", "https://api.dicebear.com/7.x/bottts/png?seed=HeartGlow", listOf(Color(0xFFF43F5E), Color(0xFFFB7185))),
        StickerItem("s7", "Hacker Mood", "Tech Life", "👨‍💻⚡", "CODE", "https://api.dicebear.com/7.x/bottts/png?seed=HackerMood", listOf(Color(0xFF10B981), Color(0xFF059669))),
        StickerItem("s8", "Sleeping Cat", "Aura Cats", "😻💤", "COZY", "https://api.dicebear.com/7.x/bottts/png?seed=SleepingCat", listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))),
        StickerItem("s9", "Alien Pulse", "Cyber Future", "👽🛸", "COSMIC", "https://api.dicebear.com/7.x/bottts/png?seed=AlienPulse", listOf(Color(0xFF14B8A6), Color(0xFF0D9488))),
        StickerItem("s10", "Magic Spark", "Moods & Vibes", "🔮🌟", "MAGIC", "https://api.dicebear.com/7.x/bottts/png?seed=MagicSpark", listOf(Color(0xFFA855F7), Color(0xFF7E22CE))),
        StickerItem("s11", "Coffee Fuel", "Tech Life", "☕💻", "BOOST", "https://api.dicebear.com/7.x/bottts/png?seed=CoffeeFuel", listOf(Color(0xFFD97706), Color(0xFFB45309))),
        StickerItem("s12", "Winner Cup", "Expressions", "🏆🥇", "CHAMP", "https://api.dicebear.com/7.x/bottts/png?seed=WinnerCup", listOf(Color(0xFFEAB308), Color(0xFFCA8A04)))
    )
}

@Composable
fun StickerKeyboardLayout(
    themePalette: ImeThemePalette,
    searchQuery: String = "",
    onQueryChange: (String) -> Unit = {},
    onSelectSticker: (StickerItem) -> Unit,
    onBackToAlpha: () -> Unit
) {
    var selectedPack by remember { mutableStateOf("All") }

    val filteredStickers = remember(selectedPack, searchQuery) {
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            StickerData.ITEMS.filter {
                it.name.lowercase().contains(q) ||
                it.packName.lowercase().contains(q) ||
                it.badge.lowercase().contains(q)
            }
        } else if (selectedPack == "All") {
            StickerData.ITEMS
        } else {
            StickerData.ITEMS.filter { it.packName.equals(selectedPack, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // TOP HEADER + BACK ACTION
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
                    contentDescription = "Search Stickers",
                    tint = themePalette.accentSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (searchQuery.isEmpty()) "Type on keyboard to search stickers..." else searchQuery,
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

        // PACK CHIPS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StickerData.PACKS.forEach { pack ->
                val isSelected = selectedPack == pack && searchQuery.isEmpty()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) themePalette.accentSecondary else themePalette.keyBackgroundPressed)
                        .border(
                            0.5.dp,
                            if (isSelected) themePalette.accentSecondary else themePalette.border.copy(alpha = 0.3f),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            selectedPack = pack
                            if (searchQuery.isNotEmpty()) {
                                onQueryChange("")
                            }
                        }
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = pack,
                        style = TextStyle(
                            color = if (isSelected) Color.White else themePalette.keySubtext,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        // STICKER GRID RESULTS
        if (filteredStickers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No stickers found for \"$searchQuery\".",
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
                items(filteredStickers, key = { it.id }) { sticker ->
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(sticker.backgroundGradient))
                            .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .clickable { onSelectSticker(sticker) }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = sticker.emoji,
                                fontSize = 18.sp
                            )
                            Text(
                                text = sticker.name,
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.SemiBold
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
