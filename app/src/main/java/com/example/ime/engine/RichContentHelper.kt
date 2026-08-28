package com.example.ime.engine

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import com.example.data.klipy.KlipyMediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * Rich Content & Media Insertion Engine for Android IMEs.
 * 
 * Supports:
 * - Direct content URI generation via FileProvider for WhatsApp, Telegram, Gboard-compatible apps
 * - Capability negotiation via EditorInfoCompat.getContentMimeTypes()
 * - Permission granting via InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
 * - High-speed disk caching to avoid re-downloading media
 * - Graceful fallback to text insertion and clipboard copying if rich content is unsupported
 * - Full null-safety and exception catching so keyboard never crashes
 */
object RichContentHelper {

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Checks if the active target editor supports the specified MIME type.
     */
    fun isMimeTypeSupported(editorInfo: EditorInfo?, mimeType: String): Boolean {
        if (editorInfo == null) return false
        val supportedTypes = EditorInfoCompat.getContentMimeTypes(editorInfo)
        if (supportedTypes.isEmpty()) return false
        
        for (supported in supportedTypes) {
            if (ClipDescriptionCompat.compareMimeTypes(mimeType, supported)) {
                return true
            }
        }
        return false
    }

    /**
     * Commits a KLIPY GIF or Sticker item to the active InputConnection.
     */
    fun commitKlipyMedia(
        context: Context,
        scope: CoroutineScope,
        inputConnection: InputConnection?,
        editorInfo: EditorInfo?,
        mediaItem: KlipyMediaItem,
        onResult: (Boolean) -> Unit = {}
    ) {
        if (inputConnection == null) {
            onResult(false)
            return
        }

        val directUrl = mediaItem.resolveDirectMediaUrl(preferSmall = false)
        val linkUrl = mediaItem.url ?: directUrl
        val title = mediaItem.title ?: "Aura Media"
        val isSticker = mediaItem.type == "sticker"
        
        val mimeType = when {
            directUrl.endsWith(".webp", ignoreCase = true) -> "image/webp"
            directUrl.endsWith(".png", ignoreCase = true) -> "image/png"
            directUrl.endsWith(".jpg", ignoreCase = true) || directUrl.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
            else -> "image/gif"
        }

        if (directUrl.isBlank()) {
            // Fallback: Commit link text if no media URL
            try {
                if (linkUrl.isNotBlank()) {
                    inputConnection.commitText(linkUrl, 1)
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
            return
        }

        // Run download and commit asynchronously
        scope.launch(Dispatchers.IO) {
            var committed = false
            try {
                // 1. Download/fetch local cached file
                val file = getOrDownloadMediaFile(context, directUrl, mimeType)
                if (file != null && file.exists() && file.length() > 0) {
                    val authority = "${context.packageName}.fileprovider"
                    val contentUri = FileProvider.getUriForFile(context, authority, file)

                    // 2. Build InputContentInfoCompat
                    val description = android.content.ClipDescription(title, arrayOf(mimeType, "image/*"))
                    val contentInfo = InputContentInfoCompat(
                        contentUri,
                        description,
                        Uri.parse(linkUrl.ifBlank { directUrl })
                    )

                    var flags = 0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        flags = flags or InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
                    }

                    // 3. Try InputConnectionCompat.commitContent on Main thread
                    withContext(Dispatchers.Main) {
                        try {
                            if (editorInfo != null) {
                                committed = InputConnectionCompat.commitContent(
                                    inputConnection,
                                    editorInfo,
                                    contentInfo,
                                    flags,
                                    null
                                )
                            }
                        } catch (e: Exception) {
                            committed = false
                        }
                    }
                }
            } catch (e: Exception) {
                committed = false
            }

            // 4. If direct commitContent failed or wasn't supported by target app (e.g. standard Plain Text input)
            if (!committed) {
                withContext(Dispatchers.Main) {
                    try {
                        // Fallback: copy link to clipboard & commit URL
                        val clipManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        val clip = ClipData.newPlainText(title, linkUrl.ifBlank { directUrl })
                        clipManager?.setPrimaryClip(clip)

                        // Commit link URL into text field
                        inputConnection.commitText(linkUrl.ifBlank { directUrl }, 1)
                        
                        Toast.makeText(context, "Media link inserted (Copied to clipboard)", Toast.LENGTH_SHORT).show()
                        committed = true
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }

            withContext(Dispatchers.Main) {
                onResult(committed)
            }
        }
    }

    private fun getOrDownloadMediaFile(context: Context, urlString: String, mimeType: String): File? {
        return try {
            val extension = when (mimeType) {
                "image/webp" -> ".webp"
                "image/png" -> ".png"
                "image/jpeg" -> ".jpg"
                else -> ".gif"
            }

            val hash = hashUrl(urlString)
            val cacheDir = File(context.cacheDir, "media_shares").apply { mkdirs() }
            val targetFile = File(cacheDir, "aura_$hash$extension")

            if (targetFile.exists() && targetFile.length() > 0) {
                return targetFile
            }

            // Download file
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            connection.instanceFollowRedirects = true
            connection.connect()

            if (connection.responseCode in 200..299) {
                connection.inputStream.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                targetFile
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun hashUrl(input: String): String {
        return try {
            val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            input.hashCode().toString()
        }
    }
}

private object ClipDescriptionCompat {
    fun compareMimeTypes(concreteType: String, desiredType: String): Boolean {
        if (concreteType == desiredType || desiredType == "*/*") return true
        if (desiredType.endsWith("/*")) {
            val prefix = desiredType.substring(0, desiredType.indexOf('/'))
            return concreteType.startsWith("$prefix/")
        }
        return false
    }
}
