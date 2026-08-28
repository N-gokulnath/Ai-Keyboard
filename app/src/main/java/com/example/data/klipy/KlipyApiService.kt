package com.example.data.klipy

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for official KLIPY API v1 endpoints
 * Base URL: https://api.klipy.com
 */
interface KlipyApiService {

    // --- GIF ENDPOINTS ---

    @GET("api/v1/{app_key}/gifs/trending")
    suspend fun getTrendingGifs(
        @Path("app_key") appKey: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 24,
        @Query("locale") locale: String? = "en",
        @Query("content_filter") contentFilter: String? = "medium",
        @Query("customer_id") customerId: String? = null
    ): Response<KlipyEnvelope<KlipyPaginatedData<KlipyMediaItem>>>

    @GET("api/v1/{app_key}/gifs/search")
    suspend fun searchGifs(
        @Path("app_key") appKey: String,
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 24,
        @Query("locale") locale: String? = "en",
        @Query("content_filter") contentFilter: String? = "medium",
        @Query("customer_id") customerId: String? = null
    ): Response<KlipyEnvelope<KlipyPaginatedData<KlipyMediaItem>>>

    @GET("api/v1/{app_key}/gifs/categories")
    suspend fun getGifCategories(
        @Path("app_key") appKey: String,
        @Query("locale") locale: String? = "en"
    ): Response<KlipyEnvelope<KlipyCategoriesData>>

    // --- STICKER ENDPOINTS ---

    @GET("api/v1/{app_key}/stickers/trending")
    suspend fun getTrendingStickers(
        @Path("app_key") appKey: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 24,
        @Query("locale") locale: String? = "en",
        @Query("content_filter") contentFilter: String? = "medium",
        @Query("customer_id") customerId: String? = null
    ): Response<KlipyEnvelope<KlipyPaginatedData<KlipyMediaItem>>>

    @GET("api/v1/{app_key}/stickers/search")
    suspend fun searchStickers(
        @Path("app_key") appKey: String,
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 24,
        @Query("locale") locale: String? = "en",
        @Query("content_filter") contentFilter: String? = "medium",
        @Query("customer_id") customerId: String? = null
    ): Response<KlipyEnvelope<KlipyPaginatedData<KlipyMediaItem>>>

    @GET("api/v1/{app_key}/stickers/categories")
    suspend fun getStickerCategories(
        @Path("app_key") appKey: String,
        @Query("locale") locale: String? = "en"
    ): Response<KlipyEnvelope<KlipyCategoriesData>>
}

