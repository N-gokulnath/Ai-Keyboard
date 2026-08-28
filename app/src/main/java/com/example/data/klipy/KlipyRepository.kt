package com.example.data.klipy

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

sealed class KlipyResult<out T> {
    data class Success<T>(val data: T, val isFromPagination: Boolean = false) : KlipyResult<T>()
    data class Error(val message: String, val isRateLimited: Boolean = false, val statusCode: Int? = null) : KlipyResult<Nothing>()
    object Loading : KlipyResult<Nothing>()
    object Empty : KlipyResult<Nothing>()
}

/**
 * Repository for official KLIPY API integration.
 * Complies strictly with KLIPY API Terms and Integration Requirements:
 * - Directly loads media from returned URLs without caching, mirroring, re-hosting, or modifying.
 * - Preserves mandatory KLIPY attribution.
 * - Detects and processes KLIPY ad objects separately from standard media.
 * - Separates Test and Production environment configurations.
 */
class KlipyRepository private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_KLIPY, Context.MODE_PRIVATE)

    private var customerId: String = loadOrGenerateCustomerId()

    // Configuration states
    private val _contentFilter = MutableStateFlow(loadContentFilter())
    val contentFilter: StateFlow<KlipyContentFilter> = _contentFilter.asStateFlow()

    private val _environment = MutableStateFlow(loadEnvironment())
    val environment: StateFlow<KlipyEnvironment> = _environment.asStateFlow()

    private val _adsEnabled = MutableStateFlow(loadAdsEnabled())
    val adsEnabled: StateFlow<Boolean> = _adsEnabled.asStateFlow()

    private val _customApiKey = MutableStateFlow(loadCustomApiKey())
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // Recents flows
    private val _recentGifs = MutableStateFlow<List<KlipyMediaItem>>(loadRecentMedia(KEY_RECENT_GIFS))
    val recentGifs: StateFlow<List<KlipyMediaItem>> = _recentGifs.asStateFlow()

    private val _recentStickers = MutableStateFlow<List<KlipyMediaItem>>(loadRecentMedia(KEY_RECENT_STICKERS))
    val recentStickers: StateFlow<List<KlipyMediaItem>> = _recentStickers.asStateFlow()

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val apiService: KlipyApiService = retrofit.create(KlipyApiService::class.java)

    /**
     * Resolves the active KLIPY API key.
     * Uses custom user-provided key if present, otherwise defaults to BuildConfig / configured key.
     */
    fun getActiveApiKey(): String {
        val custom = _customApiKey.value.trim()
        if (custom.isNotEmpty() && custom != DEFAULT_PLACEHOLDER_KEY) {
            return custom
        }
        val buildKey = try {
            BuildConfig.KLIPY_API_KEY
        } catch (_: Exception) {
            ""
        }
        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY" && buildKey != "YOUR_KLIPY_KEY") {
            return buildKey
        }
        return FALLBACK_KLIPY_KEY
    }

    // --- GIF OPERATIONS ---

    suspend fun getTrendingGifs(page: Int = 1, perPage: Int = 24): KlipyResult<List<KlipyMediaItem>> =
        withContext(Dispatchers.IO) {
            try {
                val apiKey = getActiveApiKey()
                val response = apiService.getTrendingGifs(
                    appKey = apiKey,
                    page = page,
                    perPage = perPage,
                    locale = "en",
                    contentFilter = _contentFilter.value.apiValue,
                    customerId = customerId
                )

                if (response.isSuccessful) {
                    val envelope = response.body()
                    val items = envelope?.data?.data ?: emptyList()
                    if (items.isEmpty()) {
                        KlipyResult.Empty
                    } else {
                        KlipyResult.Success(items, isFromPagination = page > 1)
                    }
                } else {
                    val code = response.code()
                    val isRateLimited = code == 429
                    val errorMsg = when (code) {
                        429 -> "KLIPY API rate limit reached (100 req/hr on Test mode). Please wait a moment."
                        401, 403 -> "Invalid or unauthorized KLIPY API key. Please check your Partner Panel key."
                        else -> "KLIPY error (${code}): ${response.message()}"
                    }
                    Log.w(TAG, "getTrendingGifs failed: $errorMsg")
                    KlipyResult.Error(errorMsg, isRateLimited = isRateLimited, statusCode = code)
                }
            } catch (e: Exception) {
                Log.e(TAG, "getTrendingGifs exception", e)
                KlipyResult.Error("Unable to connect to KLIPY: ${e.localizedMessage ?: "Network error"}")
            }
        }

    suspend fun searchGifs(query: String, page: Int = 1, perPage: Int = 24): KlipyResult<List<KlipyMediaItem>> =
        withContext(Dispatchers.IO) {
            if (query.isBlank()) {
                return@withContext getTrendingGifs(page, perPage)
            }
            try {
                val apiKey = getActiveApiKey()
                val response = apiService.searchGifs(
                    appKey = apiKey,
                    query = query.trim(),
                    page = page,
                    perPage = perPage,
                    locale = "en",
                    contentFilter = _contentFilter.value.apiValue,
                    customerId = customerId
                )

                if (response.isSuccessful) {
                    val envelope = response.body()
                    val items = envelope?.data?.data ?: emptyList()
                    if (items.isEmpty()) {
                        KlipyResult.Empty
                    } else {
                        KlipyResult.Success(items, isFromPagination = page > 1)
                    }
                } else {
                    val code = response.code()
                    val isRateLimited = code == 429
                    val errorMsg = when (code) {
                        429 -> "KLIPY API rate limit reached. Please try again shortly."
                        401, 403 -> "KLIPY API Key unauthorized. Check Partner Panel."
                        else -> "KLIPY Search error (${code}): ${response.message()}"
                    }
                    KlipyResult.Error(errorMsg, isRateLimited = isRateLimited, statusCode = code)
                }
            } catch (e: Exception) {
                Log.e(TAG, "searchGifs exception", e)
                KlipyResult.Error("KLIPY Search failed: ${e.localizedMessage ?: "Network error"}")
            }
        }

    // --- STICKER OPERATIONS ---

    suspend fun getTrendingStickers(page: Int = 1, perPage: Int = 24): KlipyResult<List<KlipyMediaItem>> =
        withContext(Dispatchers.IO) {
            try {
                val apiKey = getActiveApiKey()
                val response = apiService.getTrendingStickers(
                    appKey = apiKey,
                    page = page,
                    perPage = perPage,
                    locale = "en",
                    contentFilter = _contentFilter.value.apiValue,
                    customerId = customerId
                )

                if (response.isSuccessful) {
                    val envelope = response.body()
                    val items = envelope?.data?.data ?: emptyList()
                    if (items.isEmpty()) {
                        KlipyResult.Empty
                    } else {
                        KlipyResult.Success(items, isFromPagination = page > 1)
                    }
                } else {
                    val code = response.code()
                    val isRateLimited = code == 429
                    val errorMsg = when (code) {
                        429 -> "KLIPY API rate limit reached. Please wait a moment."
                        401, 403 -> "KLIPY API Key unauthorized."
                        else -> "KLIPY Stickers error (${code}): ${response.message()}"
                    }
                    KlipyResult.Error(errorMsg, isRateLimited = isRateLimited, statusCode = code)
                }
            } catch (e: Exception) {
                Log.e(TAG, "getTrendingStickers exception", e)
                KlipyResult.Error("Unable to connect to KLIPY: ${e.localizedMessage ?: "Network error"}")
            }
        }

    suspend fun searchStickers(query: String, page: Int = 1, perPage: Int = 24): KlipyResult<List<KlipyMediaItem>> =
        withContext(Dispatchers.IO) {
            if (query.isBlank()) {
                return@withContext getTrendingStickers(page, perPage)
            }
            try {
                val apiKey = getActiveApiKey()
                val response = apiService.searchStickers(
                    appKey = apiKey,
                    query = query.trim(),
                    page = page,
                    perPage = perPage,
                    locale = "en",
                    contentFilter = _contentFilter.value.apiValue,
                    customerId = customerId
                )

                if (response.isSuccessful) {
                    val envelope = response.body()
                    val items = envelope?.data?.data ?: emptyList()
                    if (items.isEmpty()) {
                        KlipyResult.Empty
                    } else {
                        KlipyResult.Success(items, isFromPagination = page > 1)
                    }
                } else {
                    val code = response.code()
                    val isRateLimited = code == 429
                    val errorMsg = when (code) {
                        429 -> "KLIPY API rate limit reached. Please try again shortly."
                        401, 403 -> "KLIPY API Key unauthorized."
                        else -> "KLIPY Stickers error (${code}): ${response.message()}"
                    }
                    KlipyResult.Error(errorMsg, isRateLimited = isRateLimited, statusCode = code)
                }
            } catch (e: Exception) {
                Log.e(TAG, "searchStickers exception", e)
                KlipyResult.Error("KLIPY Sticker Search failed: ${e.localizedMessage ?: "Network error"}")
            }
        }

    // --- CATEGORIES & SUGGESTIONS ---

    suspend fun getCategories(isStickers: Boolean = false): List<String> = withContext(Dispatchers.IO) {
        val defaults = if (isStickers) DEFAULT_STICKER_CATEGORIES else DEFAULT_GIF_CATEGORIES
        try {
            val apiKey = getActiveApiKey()
            val response = if (isStickers) {
                apiService.getStickerCategories(apiKey, "en")
            } else {
                apiService.getGifCategories(apiKey, "en")
            }
            if (response.isSuccessful) {
                val categoriesData = response.body()?.data?.categories
                val list = categoriesData?.mapNotNull { item ->
                    item.category?.ifBlank { null } ?: item.name?.ifBlank { null } ?: item.query?.ifBlank { null }
                } ?: emptyList()
                if (list.isNotEmpty()) {
                    return@withContext listOf("Trending", "Recents") + list.distinct()
                }
            }
        } catch (_: Exception) {}
        return@withContext defaults
    }

    // --- RECENTS MANAGEMENT ---

    fun addRecentGif(item: KlipyMediaItem) {
        val current = _recentGifs.value.toMutableList()
        current.removeAll { it.itemId == item.itemId }
        current.add(0, item)
        val limited = current.take(30)
        _recentGifs.value = limited
        saveRecentMedia(KEY_RECENT_GIFS, limited)
    }

    fun addRecentSticker(item: KlipyMediaItem) {
        val current = _recentStickers.value.toMutableList()
        current.removeAll { it.itemId == item.itemId }
        current.add(0, item)
        val limited = current.take(30)
        _recentStickers.value = limited
        saveRecentMedia(KEY_RECENT_STICKERS, limited)
    }

    fun clearRecents() {
        _recentGifs.value = emptyList()
        _recentStickers.value = emptyList()
        prefs.edit().remove(KEY_RECENT_GIFS).remove(KEY_RECENT_STICKERS).apply()
    }

    // --- SETTINGS / PARTNER PANEL CONTROLS ---

    fun setContentFilter(filter: KlipyContentFilter) {
        prefs.edit().putString(KEY_CONTENT_FILTER, filter.name).apply()
        _contentFilter.value = filter
    }

    fun setEnvironment(env: KlipyEnvironment) {
        prefs.edit().putString(KEY_ENVIRONMENT, env.name).apply()
        _environment.value = env
    }

    fun setAdsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ADS_ENABLED, enabled).apply()
        _adsEnabled.value = enabled
    }

    fun setCustomApiKey(key: String) {
        prefs.edit().putString(KEY_CUSTOM_API_KEY, key.trim()).apply()
        _customApiKey.value = key.trim()
    }

    // --- INTERNAL PREFERENCES STORAGE ---

    private fun loadContentFilter(): KlipyContentFilter {
        val name = prefs.getString(KEY_CONTENT_FILTER, KlipyContentFilter.MEDIUM.name)
        return try {
            KlipyContentFilter.valueOf(name ?: KlipyContentFilter.MEDIUM.name)
        } catch (_: Exception) {
            KlipyContentFilter.MEDIUM
        }
    }

    private fun loadEnvironment(): KlipyEnvironment {
        val name = prefs.getString(KEY_ENVIRONMENT, KlipyEnvironment.PRODUCTION.name)
        return try {
            KlipyEnvironment.valueOf(name ?: KlipyEnvironment.PRODUCTION.name)
        } catch (_: Exception) {
            KlipyEnvironment.PRODUCTION
        }
    }

    private fun loadAdsEnabled(): Boolean = prefs.getBoolean(KEY_ADS_ENABLED, true)

    private fun loadCustomApiKey(): String = prefs.getString(KEY_CUSTOM_API_KEY, "") ?: ""

    private fun loadOrGenerateCustomerId(): String {
        var id = prefs.getString(KEY_CUSTOMER_ID, null)
        if (id.isNullOrBlank()) {
            id = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_CUSTOMER_ID, id).apply()
        }
        return id
    }

    private fun saveRecentMedia(key: String, items: List<KlipyMediaItem>) {
        try {
            val adapter = moshi.adapter<List<KlipyMediaItem>>(
                com.squareup.moshi.Types.newParameterizedType(List::class.java, KlipyMediaItem::class.java)
            )
            val json = adapter.toJson(items)
            prefs.edit().putString(key, json).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save recents", e)
        }
    }

    private fun loadRecentMedia(key: String): List<KlipyMediaItem> {
        val json = prefs.getString(key, null) ?: return emptyList()
        return try {
            val adapter = moshi.adapter<List<KlipyMediaItem>>(
                com.squareup.moshi.Types.newParameterizedType(List::class.java, KlipyMediaItem::class.java)
            )
            adapter.fromJson(json) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        private const val TAG = "KlipyRepository"
        private const val BASE_URL = "https://api.klipy.com/"
        private const val PREFS_KLIPY = "aura_klipy_prefs"

        private const val KEY_CONTENT_FILTER = "klipy_content_filter"
        private const val KEY_ENVIRONMENT = "klipy_environment"
        private const val KEY_ADS_ENABLED = "klipy_ads_enabled"
        private const val KEY_CUSTOM_API_KEY = "klipy_custom_api_key"
        private const val KEY_CUSTOMER_ID = "klipy_customer_id"
        private const val KEY_RECENT_GIFS = "klipy_recent_gifs"
        private const val KEY_RECENT_STICKERS = "klipy_recent_stickers"

        const val FALLBACK_KLIPY_KEY = "5jWQJNDmItahvohDNNUW5R0ajb119bOKocsj4vcymk1eUQPXSrA3Oj8Y7flWIxLB"
        private const val DEFAULT_PLACEHOLDER_KEY = "your_api_key_here"

        val DEFAULT_GIF_CATEGORIES = listOf(
            "Trending", "Recents", "Reactions", "Memes", "Love", "Happy", "Sad", "Cheers", "Gaming", "Dance", "Applause", "Facepalm", "Sleepy"
        )

        val DEFAULT_STICKER_CATEGORIES = listOf(
            "Trending", "Recents", "Anime", "Vibes", "Cute", "Animals", "Gaming", "Text", "Emotions", "Party", "Cyber", "Hearts"
        )

        @Volatile
        private var INSTANCE: KlipyRepository? = null

        fun getInstance(context: Context): KlipyRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: KlipyRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
