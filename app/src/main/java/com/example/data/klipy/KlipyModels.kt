package com.example.data.klipy

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

/**
 * KLIPY Media Format Representation (HD, MD, SM, XS, WebP, GIF, MP4, WebM, etc.)
 */
@JsonClass(generateAdapter = true)
data class KlipyMediaFormat(
    @Json(name = "url") val url: String? = null,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null,
    @Json(name = "size") val size: Long? = null
)

/**
 * File formats available for each resolution level
 */
@JsonClass(generateAdapter = true)
data class KlipyFileFormats(
    @Json(name = "gif") val gif: KlipyMediaFormat? = null,
    @Json(name = "webp") val webp: KlipyMediaFormat? = null,
    @Json(name = "mp4") val mp4: KlipyMediaFormat? = null,
    @Json(name = "webm") val webm: KlipyMediaFormat? = null,
    @Json(name = "png") val png: KlipyMediaFormat? = null,
    @Json(name = "jpg") val jpg: KlipyMediaFormat? = null
)

/**
 * Resolution ladder provided by KLIPY
 */
@JsonClass(generateAdapter = true)
data class KlipyMediaResolutions(
    @Json(name = "hd") val hd: KlipyFileFormats? = null,
    @Json(name = "md") val md: KlipyFileFormats? = null,
    @Json(name = "sm") val sm: KlipyFileFormats? = null,
    @Json(name = "xs") val xs: KlipyFileFormats? = null,
    @Json(name = "tiny") val tiny: KlipyFileFormats? = null
)

/**
 * KLIPY Ad Object for monetization and ad rendering
 */
@JsonClass(generateAdapter = true)
data class KlipyAdObject(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "advertiser") val advertiser: String? = null,
    @Json(name = "click_url") val clickUrl: String? = null,
    @Json(name = "impression_url") val impressionUrl: String? = null,
    @Json(name = "media_url") val mediaUrl: String? = null,
    @Json(name = "type") val type: String? = "ad"
)

/**
 * KLIPY Unified Media Item (GIFs, Stickers, Memes, Ads)
 */
@JsonClass(generateAdapter = true)
data class KlipyMediaItem(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "type") val type: String? = "gif", // "gif", "sticker", "clip", "meme", "ad"
    @Json(name = "url") val url: String? = null,
    @Json(name = "file") val file: KlipyMediaResolutions? = null,
    @Json(name = "is_ad") val isAd: Boolean? = false,
    @Json(name = "ad") val ad: KlipyAdObject? = null,
    @Json(name = "tags") val tags: List<String>? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "blur_preview") val blurPreview: String? = null
) {
    /**
     * Unique ID string representation
     */
    val itemId: String
        get() = (id ?: slug ?: UUID.randomUUID()).toString()

    /**
     * Determines if this item is a KLIPY sponsored/ad object
     */
    val isSponsored: Boolean
        get() = isAd == true || type == "ad" || ad != null

    /**
     * Resolves the direct media URL from KLIPY without caching or modifying.
     * Prioritizes direct GIF/WebP URLs across resolution ladders.
     */
    fun resolveDirectMediaUrl(preferSmall: Boolean = false): String {
        if (ad?.mediaUrl?.isNotBlank() == true) {
            return ad.mediaUrl
        }

        if (preferSmall) {
            // Prefer SM/XS for compact keyboard grid views
            file?.sm?.gif?.url?.let { return it }
            file?.sm?.webp?.url?.let { return it }
            file?.xs?.gif?.url?.let { return it }
            file?.xs?.webp?.url?.let { return it }
            file?.md?.gif?.url?.let { return it }
            file?.md?.webp?.url?.let { return it }
            file?.sm?.png?.url?.let { return it }
            file?.hd?.gif?.url?.let { return it }
            file?.hd?.webp?.url?.let { return it }
        } else {
            // Prefer MD/HD for insertion into input fields / rich content
            file?.md?.gif?.url?.let { return it }
            file?.hd?.gif?.url?.let { return it }
            file?.md?.webp?.url?.let { return it }
            file?.hd?.webp?.url?.let { return it }
            file?.sm?.gif?.url?.let { return it }
            file?.sm?.webp?.url?.let { return it }
            file?.hd?.png?.url?.let { return it }
            file?.md?.png?.url?.let { return it }
        }

        if (!url.isNullOrBlank()) {
            return url
        }

        return ""
    }

    /**
     * Preview thumbnail URL for compact rendering
     */
    fun resolveThumbnailUrl(): String {
        return resolveDirectMediaUrl(preferSmall = true)
    }
}

/**
 * KLIPY Paginated Data Structure (under `data` key)
 */
@JsonClass(generateAdapter = true)
data class KlipyPaginatedData<T>(
    @Json(name = "data") val data: List<T>? = null,
    @Json(name = "current_page") val currentPage: Int? = null,
    @Json(name = "per_page") val perPage: Int? = null,
    @Json(name = "has_next") val hasNext: Boolean? = null,
    @Json(name = "total") val total: Int? = null
)

/**
 * KLIPY Category item inside categories response
 */
@JsonClass(generateAdapter = true)
data class KlipyCategoryItem(
    @Json(name = "category") val category: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "query") val query: String? = null,
    @Json(name = "preview_url") val previewUrl: String? = null
)

/**
 * KLIPY Categories Response container
 */
@JsonClass(generateAdapter = true)
data class KlipyCategoriesData(
    @Json(name = "locale") val locale: String? = null,
    @Json(name = "categories") val categories: List<KlipyCategoryItem>? = null
)

/**
 * KLIPY API Root Response Envelope
 */
@JsonClass(generateAdapter = true)
data class KlipyEnvelope<T>(
    @Json(name = "result") val result: Boolean? = true,
    @Json(name = "data") val data: T? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "code") val code: Int? = null
)

/**
 * Content safety filter levels for Partner Panel configuration
 */
enum class KlipyContentFilter(val apiValue: String, val displayName: String) {
    OFF("off", "Off (Unrestricted)"),
    LOW("low", "Low (Broad Audience)"),
    MEDIUM("medium", "Medium (Balanced - Recommended)"),
    HIGH("high", "High (Strict Family-Safe)")
}

/**
 * Environment configuration: Test mode (100 req/hr rate limit) vs Production
 */
enum class KlipyEnvironment(val displayName: String, val maxRequestsPerHourText: String) {
    TEST("Test Mode", "100 requests/hour limit"),
    PRODUCTION("Production Mode", "Unlimited requests")
}

