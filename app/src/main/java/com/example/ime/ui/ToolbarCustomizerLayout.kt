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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToolbarCustomizerLayout(
    themePalette: ImeThemePalette,
    pinnedToolIds: List<String>,
    onUpdatePinnedTools: (List<String>) -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(themePalette.keyBackgroundPressed)
                        .clickable { onDone() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Done",
                        tint = themePalette.keyText,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Text(
                    text = "Customize Toolbar",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = themePalette.keyText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // RESET
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(themePalette.keyBackgroundPressed)
                        .clickable { onUpdatePinnedTools(KeyboardTools.DEFAULT_PINNED_IDS) }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = themePalette.keySubtext,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Reset",
                            color = themePalette.keySubtext,
                            fontSize = 10.sp
                        )
                    }
                }

                // DONE
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(themePalette.accentPrimary)
                        .clickable { onDone() }
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Done",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }

        // PINNED TOOLS ROW PREVIEW
        Text(
            text = "PINNED TOOLS (max 5 in top bar):",
            style = MaterialTheme.typography.labelSmall.copy(
                color = themePalette.keySubtext,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(themePalette.keyBackground)
                .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (pinnedToolIds.isEmpty()) {
                Text(
                    text = "No pinned tools. Tap (+) below to add.",
                    color = themePalette.keySubtext.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            } else {
                pinnedToolIds.forEach { toolId ->
                    val tool = KeyboardTools.getToolById(toolId)
                    if (tool != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(themePalette.keyBackgroundPressed)
                                .border(0.5.dp, themePalette.border.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = tool.icon,
                                    contentDescription = tool.title,
                                    tint = themePalette.accentPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = tool.shortName,
                                    color = themePalette.keyText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                        .clickable {
                                            val updated = pinnedToolIds.toMutableList()
                                            updated.remove(toolId)
                                            onUpdatePinnedTools(updated)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(9.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // AVAILABLE TOOLS GRID
        Text(
            text = "ALL AVAILABLE TOOLS:",
            style = MaterialTheme.typography.labelSmall.copy(
                color = themePalette.keySubtext,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(KeyboardTools.ALL_TOOLS.filter { it.id != "customize" }, key = { it.id }) { tool ->
                val isPinned = pinnedToolIds.contains(tool.id)
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isPinned) themePalette.keyBackgroundPressed else themePalette.keyBackground)
                        .border(
                            0.5.dp,
                            if (isPinned) themePalette.accentPrimary.copy(alpha = 0.5f) else themePalette.border.copy(alpha = 0.3f),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            val updated = pinnedToolIds.toMutableList()
                            if (isPinned) {
                                updated.remove(tool.id)
                            } else {
                                if (updated.size < 6) {
                                    updated.add(tool.id)
                                }
                            }
                            onUpdatePinnedTools(updated)
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = tool.title,
                                tint = if (isPinned) themePalette.accentPrimary else themePalette.keySubtext,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = tool.shortName,
                                color = if (isPinned) themePalette.keyText else themePalette.keySubtext,
                                fontSize = 10.sp,
                                fontWeight = if (isPinned) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = if (isPinned) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = if (isPinned) "Pinned" else "Pin",
                            tint = if (isPinned) themePalette.accentPrimary else themePalette.keySubtext,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
