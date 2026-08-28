package com.example.ime.ui.klipy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.klipy.KlipyMediaItem
import com.example.ime.ui.ImeThemePalette

/**
 * Mandatory KLIPY Attribution Badge
 * Complies with official KLIPY API integration guidelines.
 */
@Composable
fun KlipyAttributionBadge(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isDark) Color(0x33000000) else Color(0x1A000000))
            .padding(horizontal = 5.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = "POWERED BY",
            style = TextStyle(
                color = if (isDark) Color(0x99FFFFFF) else Color(0x99000000),
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        )
        // KLIPY Brand text with vibrant gradient accent
        Text(
            text = "KLIPY",
            style = TextStyle(
                color = Color(0xFF6366F1),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
            )
        )
    }
}

/**
 * Sponsored / Ad Badge for KLIPY Ad Objects
 * Renders distinct monetization indicators required by KLIPY Ads API.
 */
@Composable
fun KlipyAdBadge(
    modifier: Modifier = Modifier,
    advertiserName: String? = null
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xE6EAB308))
            .padding(horizontal = 4.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "AD",
            style = TextStyle(
                color = Color.Black,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.ExtraBold
            )
        )
        if (!advertiserName.isNullOrBlank()) {
            Text(
                text = "• $advertiserName",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
        }
    }
}

/**
 * Loading state indicator for KLIPY grids
 */
@Composable
fun KlipyLoadingView(
    themePalette: ImeThemePalette,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = themePalette.accentPrimary,
                strokeWidth = 2.5.dp
            )
            Text(
                text = "Loading KLIPY media...",
                style = TextStyle(
                    color = themePalette.keySubtext,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

/**
 * Error & Rate Limit state view with Retry button
 */
@Composable
fun KlipyErrorView(
    message: String,
    isRateLimited: Boolean,
    themePalette: ImeThemePalette,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Icon(
                imageVector = if (isRateLimited) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = "Error",
                tint = if (isRateLimited) Color(0xFFF59E0B) else themePalette.accentSecondary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = message,
                style = TextStyle(
                    color = themePalette.keyText,
                    fontSize = 10.5.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 2
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(themePalette.accentPrimary)
                    .clickable { onRetry() }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Retry",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/**
 * Empty results state view
 */
@Composable
fun KlipyEmptyView(
    query: String,
    themePalette: ImeThemePalette,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (query.isNotBlank()) "No KLIPY results for \"$query\"" else "No items available",
                style = TextStyle(
                    color = themePalette.keyText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
            if (query.isNotBlank()) {
                Text(
                    text = "Try searching for another term or browse categories above",
                    style = TextStyle(
                        color = themePalette.keySubtext,
                        fontSize = 9.5.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
