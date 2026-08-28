package com.example.ime.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.data.klipy.KlipyMediaItem
import com.example.data.klipy.KlipyRepository
import com.example.data.klipy.KlipyResult
import com.example.ime.ui.klipy.KlipyAdBadge
import com.example.ime.ui.klipy.KlipyAttributionBadge
import com.example.ime.ui.klipy.KlipyEmptyView
import com.example.ime.ui.klipy.KlipyErrorView
import com.example.ime.ui.klipy.KlipyLoadingView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StickerKeyboardLayout(
    themePalette: ImeThemePalette,
    searchQuery: String = "",
    onQueryChange: (String) -> Unit = {},
    onSelectSticker: (KlipyMediaItem) -> Unit,
    onBackToAlpha: () -> Unit,
    repository: KlipyRepository = KlipyRepository.getInstance(LocalContext.current)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedCategory by remember { mutableStateOf("Trending") }
    var categories by remember { mutableStateOf(KlipyRepository.DEFAULT_STICKER_CATEGORIES) }

    val recentStickers by repository.recentStickers.collectAsState()

    var currentPage by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isRateLimited by remember { mutableStateOf(false) }
    var hasMorePages by remember { mutableStateOf(true) }

    val mediaItems = remember { mutableStateListOf<KlipyMediaItem>() }
    val gridState = rememberLazyGridState()

    // Fetch categories on launch
    LaunchedEffect(Unit) {
        categories = repository.getCategories(isStickers = true)
    }

    // Function to load Stickers from KLIPY
    fun loadStickers(page: Int, isInitial: Boolean = false) {
        if (selectedCategory == "Recents" && searchQuery.isEmpty()) {
            mediaItems.clear()
            mediaItems.addAll(recentStickers)
            isLoading = false
            isLoadingMore = false
            errorMessage = null
            hasMorePages = false
            return
        }

        if (isInitial) {
            isLoading = true
            errorMessage = null
            isRateLimited = false
            currentPage = 1
            hasMorePages = true
        } else {
            isLoadingMore = true
        }

        coroutineScope.launch {
            val result = if (searchQuery.isNotBlank()) {
                repository.searchStickers(searchQuery.trim(), page = page, perPage = 24)
            } else if (selectedCategory == "Trending") {
                repository.getTrendingStickers(page = page, perPage = 24)
            } else {
                repository.searchStickers(selectedCategory.lowercase(), page = page, perPage = 24)
            }

            when (result) {
                is KlipyResult.Success -> {
                    if (isInitial || page == 1) {
                        mediaItems.clear()
                    }
                    val newItems = result.data
                    val existingIds = mediaItems.map { it.itemId }.toSet()
                    val unique = newItems.filter { !existingIds.contains(it.itemId) }
                    mediaItems.addAll(unique)
                    hasMorePages = newItems.size >= 24
                    currentPage = page
                    errorMessage = null
                    isRateLimited = false
                }
                is KlipyResult.Empty -> {
                    if (isInitial || page == 1) {
                        mediaItems.clear()
                    }
                    hasMorePages = false
                    errorMessage = null
                }
                is KlipyResult.Error -> {
                    errorMessage = result.message
                    isRateLimited = result.isRateLimited
                }
                KlipyResult.Loading -> {}
            }
            isLoading = false
            isLoadingMore = false
        }
    }

    // Trigger initial search or category switch with debounce for search query
    LaunchedEffect(selectedCategory, searchQuery) {
        if (searchQuery.isNotBlank()) {
            delay(350) // Debounce search keystrokes
            loadStickers(page = 1, isInitial = true)
        } else {
            loadStickers(page = 1, isInitial = true)
        }
    }

    // Detect scrolling near bottom for infinite scroll pagination
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisible >= totalItems - 6 && !isLoading && !isLoadingMore && hasMorePages && selectedCategory != "Recents"
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            loadStickers(page = currentPage + 1, isInitial = false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // TOP SEARCH BAR + BACK ACTION + KLIPY ATTRIBUTION
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

            // Gboard-Style Search KLIPY Bar
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
                    contentDescription = "Search KLIPY",
                    tint = themePalette.accentSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (searchQuery.isEmpty()) "Search KLIPY" else searchQuery,
                    style = TextStyle(
                        color = if (searchQuery.isEmpty()) themePalette.keySubtext.copy(alpha = 0.7f) else themePalette.keyText,
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

            // Mandatory KLIPY Attribution Badge
            KlipyAttributionBadge(
                modifier = Modifier.align(Alignment.CenterVertically),
                isDark = true
            )
        }

        // CATEGORY CHIPS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.forEach { category ->
                val isSelected = (selectedCategory == category && searchQuery.isEmpty())
                val isTrending = category == "Trending"
                val isRecents = category == "Recents"

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
                            selectedCategory = category
                            if (searchQuery.isNotEmpty()) {
                                onQueryChange("")
                            }
                        }
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        if (isTrending) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Trending",
                                tint = if (isSelected) Color.White else themePalette.accentSecondary,
                                modifier = Modifier.size(11.dp)
                            )
                        } else if (isRecents) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Recents",
                                tint = if (isSelected) Color.White else themePalette.keySubtext,
                                modifier = Modifier.size(11.dp)
                            )
                        }
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
        }

        // MAIN KLIPY STICKERS GRID
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                isLoading -> {
                    KlipyLoadingView(themePalette = themePalette)
                }
                errorMessage != null && mediaItems.isEmpty() -> {
                    KlipyErrorView(
                        message = errorMessage ?: "Error loading KLIPY stickers",
                        isRateLimited = isRateLimited,
                        themePalette = themePalette,
                        onRetry = { loadStickers(page = 1, isInitial = true) }
                    )
                }
                mediaItems.isEmpty() -> {
                    KlipyEmptyView(
                        query = searchQuery,
                        themePalette = themePalette,
                        onClearQuery = { onQueryChange("") }
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(bottom = 4.dp)
                    ) {
                        items(mediaItems, key = { it.itemId }) { item ->
                            val directUrl = item.resolveDirectMediaUrl(preferSmall = true)
                            val isAd = item.isSponsored

                            Box(
                                modifier = Modifier
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(themePalette.keyBackground)
                                    .border(
                                        width = if (isAd) 1.dp else 0.5.dp,
                                        color = if (isAd) Color(0xFFEAB308) else themePalette.border.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        repository.addRecentSticker(item)
                                        onSelectSticker(item)
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Direct media URL loading via Coil
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(directUrl.ifBlank { "https://media.klipy.com/default.webp" })
                                        .decoderFactory(
                                            if (android.os.Build.VERSION.SDK_INT >= 28) ImageDecoderDecoder.Factory()
                                            else GifDecoder.Factory()
                                        )
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = item.title ?: "KLIPY Sticker",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Render KLIPY Ad Badge if item is sponsored
                                if (isAd) {
                                    KlipyAdBadge(
                                        modifier = Modifier
                                            .align(Alignment.TopStart),
                                        advertiserName = item.ad?.advertiser
                                    )
                                }
                            }
                        }

                        // Bottom Loading indicator during pagination
                        if (isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .height(54.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = themePalette.accentSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
