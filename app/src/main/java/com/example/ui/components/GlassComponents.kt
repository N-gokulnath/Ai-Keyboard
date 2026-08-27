package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuroraGradient
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.TertiaryCyanDim
import com.example.ui.theme.TertiaryCyanFixed

@Composable
fun isAppDarkTheme(): Boolean {
    return MaterialTheme.colorScheme.background == BackgroundDark
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    hasAuroraGlow: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isDark = isAppDarkTheme()
    val finalBgColor = backgroundColor ?: if (isDark) Color(0x331E2030) else Color(0xCCFFFFFF)
    val finalBorderColor = borderColor ?: if (isDark) Color(0x26FFFFFF) else Color(0x1F000000)
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = modifier
            .then(
                if (hasAuroraGlow) {
                    Modifier.drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x332FD9F4),
                                    Color(0x1A4F46E5),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.8f, size.height * 0.2f),
                                radius = size.maxDimension * 0.8f
                            )
                        )
                    }
                } else Modifier
            )
            .clip(shape)
            .background(finalBgColor)
            .border(borderWidth, finalBorderColor, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        content()
    }
}

@Composable
fun AuroraGlowBackground(
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    Box(
        modifier = modifier
            .drawBehind {
                val alpha1 = if (isDark) 0x24 else 0x14
                val alpha2 = if (isDark) 0x18 else 0x0E
                val alpha3 = if (isDark) 0x1C else 0x10
                val alpha4 = if (isDark) 0x12 else 0x0A

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(alpha1 shl 24 or 0x4F46E5),
                            Color(alpha2 shl 24 or 0x2FD9F4),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.2f, size.height * 0.15f),
                        radius = size.width * 0.9f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(alpha3 shl 24 or 0x2FD9F4),
                            Color(alpha4 shl 24 or 0x4F46E5),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.6f),
                        radius = size.width * 0.8f
                    )
                )
            }
    )
}

@Composable
fun AppTopBar(
    title: String = "Aura",
    onAvatarClick: () -> Unit = {},
    onActionClick: () -> Unit = {}
) {
    val isDark = isAppDarkTheme()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onAvatarClick)
        ) {
            // Stylized liquid avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF3525CD), Color(0xFF2FD9F4))
                        )
                    )
                    .border(1.5.dp, if (isDark) Color(0x80FFFFFF) else Color(0x403525CD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "User Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        IconButton(
            onClick = onActionClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0x1AFFFFFF) else Color(0x0F000000))
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = if (isDark) TertiaryCyanDim else Color(0xFF4F46E5),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AppBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val isDark = isAppDarkTheme()
    val navBarBg = if (isDark) Color(0xF012131F) else Color(0xF8FFFFFF)
    val navBarBorder = if (isDark) Color(0x1FFFFFFF) else Color(0x15000000)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = navBarBg,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        shadowElevation = if (isDark) 16.dp else 12.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, navBarBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Outlined.Home,
                selectedIcon = Icons.Filled.Home,
                label = "Home",
                isSelected = currentRoute == "home",
                onClick = { onNavigate("home") }
            )
            BottomNavItem(
                icon = Icons.Outlined.SmartToy,
                selectedIcon = Icons.Filled.SmartToy,
                label = "AI Hub",
                isSelected = currentRoute == "compose",
                onClick = { onNavigate("compose") }
            )
            BottomNavItem(
                icon = Icons.Outlined.Palette,
                selectedIcon = Icons.Filled.Palette,
                label = "Customize",
                isSelected = currentRoute == "themes",
                onClick = { onNavigate("themes") }
            )
            BottomNavItem(
                icon = Icons.Outlined.Settings,
                selectedIcon = Icons.Filled.Settings,
                label = "Settings",
                isSelected = currentRoute == "settings",
                onClick = { onNavigate("settings") }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isAppDarkTheme()
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1.0f, tween(200))
    val unselectedColor = if (isDark) Color(0xFF9E9DB0) else Color(0xFF6B7280)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .then(
                    if (isSelected) {
                        Modifier
                            .background(PrimaryIndigoContainer)
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    } else {
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else unselectedColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) (if (isDark) Color.White else PrimaryIndigoContainer) else unselectedColor
            )
        )
    }
}

@Composable
fun ToneChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4F46E5), Color(0xFF006A79))
                            )
                        )
                        .border(1.dp, Color(0xFF83EAFF), CircleShape)
                        .drawBehind {
                            drawCircle(
                                color = Color(0x402FD9F4),
                                radius = size.maxDimension * 0.7f
                            )
                        }
                } else {
                    Modifier
                        .background(if (isDark) Color(0x14FFFFFF) else Color(0x10000000))
                        .border(1.dp, if (isDark) Color(0x26FFFFFF) else Color(0x1A000000), CircleShape)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) Color.White else (if (isDark) Color(0xFFC7C4D8) else Color(0xFF4B5563))
            )
        )
    }
}
