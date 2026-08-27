package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.TertiaryCyanDim

@Composable
fun SimulatedKeyboardView(
    modifier: Modifier = Modifier,
    onKeyPressed: (String) -> Unit = {},
    onBackspace: () -> Unit = {},
    onSpace: () -> Unit = {},
    onSend: () -> Unit = {},
    onAiClick: () -> Unit = {},
    onSuggestionClick: (String) -> Unit = {},
    themeAccentColor: Color = Color(0xFF4F46E5)
) {
    var isShifted by remember { mutableStateOf(false) }
    var isNumeric by remember { mutableStateOf(false) }

    val row1 = if (isNumeric) listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
               else if (isShifted) listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
               else listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")

    val row2 = if (isNumeric) listOf("@", "#", "$", "%", "&", "-", "+", "(", ")")
               else if (isShifted) listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
               else listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")

    val row3 = if (isNumeric) listOf("*", "\"", "'", ":", ";", "!", "?")
               else if (isShifted) listOf("Z", "X", "C", "V", "B", "N", "M")
               else listOf("z", "x", "c", "v", "b", "n", "m")

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF141522),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            // AI Toolbar & Smart Suggestions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI Sparkle Glow Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4F46E5), Color(0xFF00505C))
                            )
                        )
                        .drawBehind {
                            drawCircle(
                                color = Color(0x662FD9F4),
                                radius = size.maxDimension * 0.7f
                            )
                        }
                        .clickable(onClick = onAiClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Compose",
                        tint = TertiaryCyanDim,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Suggestions
                Row(
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SuggestionChip("there.", onClick = { onSuggestionClick("there.") })
                    Spacer(modifier = Modifier.width(6.dp))
                    SuggestionChip("in 5 mins", onClick = { onSuggestionClick("in 5 mins") })
                    Spacer(modifier = Modifier.width(6.dp))
                    SuggestionChip("Sounds great!", onClick = { onSuggestionClick("Sounds great!") })
                }

                // Translate & More
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButtonSmall(icon = Icons.Default.Translate, onClick = onAiClick)
                    IconButtonSmall(icon = Icons.Default.MoreHoriz, onClick = onAiClick)
                }
            }

            // Keyboard Row 1
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row1.forEach { key ->
                    KeyCap(
                        text = key,
                        modifier = Modifier.weight(1f),
                        onClick = { onKeyPressed(key) }
                    )
                }
            }

            // Keyboard Row 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row2.forEach { key ->
                    KeyCap(
                        text = key,
                        modifier = Modifier.weight(1f),
                        onClick = { onKeyPressed(key) }
                    )
                }
            }

            // Keyboard Row 3
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shift Key
                KeySpecial(
                    modifier = Modifier.weight(1.3f),
                    onClick = { isShifted = !isShifted },
                    isActive = isShifted
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardCapslock,
                        contentDescription = "Shift",
                        tint = if (isShifted) TertiaryCyanDim else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                row3.forEach { key ->
                    KeyCap(
                        text = key,
                        modifier = Modifier.weight(1f),
                        onClick = { onKeyPressed(key) }
                    )
                }

                // Backspace Key
                KeySpecial(
                    modifier = Modifier.weight(1.3f),
                    onClick = onBackspace
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Keyboard Row 4 (Spacebar, Action)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ?123 Mode
                KeySpecial(
                    modifier = Modifier.weight(1.2f),
                    onClick = { isNumeric = !isNumeric }
                ) {
                    Text(
                        text = if (isNumeric) "ABC" else "?123",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Language
                KeySpecial(
                    modifier = Modifier.weight(1f),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Color(0xFFB7C8E1),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Spacebar
                Box(
                    modifier = Modifier
                        .weight(4.5f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF262738))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = onSpace
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "English",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0x66FFFFFF),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Period Key
                KeyCap(
                    text = ".",
                    modifier = Modifier.weight(1f),
                    onClick = { onKeyPressed(".") }
                )

                // Send Action Button
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(themeAccentColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = onSend
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyCap(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF2A2B3D))
            .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color.White,
                fontSize = 17.sp
            )
        )
    }
}

@Composable
private fun KeySpecial(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) Color(0xFF383952) else Color(0xFF1E1F2E))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun SuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFF27293D))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontSize = 13.sp
            )
        )
    }
}

@Composable
private fun IconButtonSmall(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0x1AFFFFFF))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFC7C4D8),
            modifier = Modifier.size(16.dp)
        )
    }
}
