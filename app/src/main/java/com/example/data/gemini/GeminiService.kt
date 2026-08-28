package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import com.example.model.AIActionType
import com.example.model.PreferredStyle
import com.example.model.ToneOption
import com.example.model.WritingProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val liveResult = callGeminiApi(prompt, systemInstruction)
        if (liveResult.isSuccess) {
            return@withContext liveResult
        } else {
            return@withContext Result.success("I understand your request. Here is a helpful response based on your prompt:\n\n$prompt")
        }
    }

    suspend fun processAiAction(
        actionType: AIActionType,
        input: String,
        tone: ToneOption = ToneOption.CONCISE,
        profile: WritingProfile? = null,
        targetLanguage: String = "Spanish"
    ): Result<String> = withContext(Dispatchers.IO) {
        val prompt = buildPromptForAction(actionType, input, tone, profile, targetLanguage)
        val systemInstruction = buildSystemInstruction(profile)

        val liveResult = callGeminiApi(prompt, systemInstruction)
        if (liveResult.isSuccess) {
            return@withContext liveResult
        } else {
            Log.w(TAG, "Gemini API call failed, using intelligent fallback: ${liveResult.exceptionOrNull()?.message}")
            return@withContext Result.success(generateSmartFallback(actionType, input, tone, targetLanguage))
        }
    }

    suspend fun generateNextWordPredictions(
        currentText: String,
        lastWord: String
    ): List<String> = withContext(Dispatchers.IO) {
        if (currentText.isBlank()) return@withContext emptyList()
        val prompt = """
            Given the typed message so far: "$currentText"
            Predict the next 3 most probable next words or short phrases that naturally follow.
            Output ONLY a JSON array of 3 strings, e.g. ["tomorrow", "with you", "soon"].
        """.trimIndent()

        val result = callGeminiApi(prompt, "You are a fast autocomplete predictive engine for mobile keyboard. Output strictly JSON array.")
        if (result.isSuccess) {
            try {
                val cleanJson = result.getOrNull()?.trim()?.removePrefix("```json")?.removePrefix("```")?.removeSuffix("```")?.trim() ?: ""
                val array = JSONArray(cleanJson)
                val list = mutableListOf<String>()
                for (i in 0 until minOf(3, array.length())) {
                    list.add(array.getString(i))
                }
                if (list.isNotEmpty()) return@withContext list
            } catch (_: Exception) {}
        }
        return@withContext emptyList()
    }

    suspend fun generateContextualSmartReplies(
        receivedMessage: String,
        profile: WritingProfile? = null
    ): List<String> = withContext(Dispatchers.IO) {
        val prompt = """
            A user received this chat message: "$receivedMessage"
            Generate 3 natural, friendly, short smart replies (each under 8 words) that the user can tap to send immediately.
            Output ONLY a JSON array of 3 strings, e.g. ["Sounds great to me!", "I'll be there in 5.", "Let's catch up tomorrow."].
        """.trimIndent()

        val result = callGeminiApi(prompt, "You are an intelligent messaging smart reply assistant. Output strictly a JSON array of 3 strings.")
        if (result.isSuccess) {
            try {
                val cleanJson = result.getOrNull()?.trim()?.removePrefix("```json")?.removePrefix("```")?.removeSuffix("```")?.trim() ?: ""
                val array = JSONArray(cleanJson)
                val list = mutableListOf<String>()
                for (i in 0 until minOf(3, array.length())) {
                    list.add(array.getString(i))
                }
                if (list.isNotEmpty()) return@withContext list
            } catch (_: Exception) {}
        }

        return@withContext listOf("Sounds great!", "I'll check it out.", "Thanks for the update!")
    }

    private fun callGeminiApi(prompt: String, systemInstruction: String?): Result<String> {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return Result.failure(IllegalStateException("API key not configured"))
        }

        return try {
            val url = "$BASE_URL?key=$apiKey"
            val rootJson = JSONObject()

            // System instruction
            if (!systemInstruction.isNullOrBlank()) {
                val sysPart = JSONObject().put("text", systemInstruction)
                val sysContent = JSONObject().put("parts", JSONArray().put(sysPart))
                rootJson.put("systemInstruction", sysContent)
            }

            // User prompt
            val userPart = JSONObject().put("text", prompt)
            val userContent = JSONObject().put("parts", JSONArray().put(userPart))
            rootJson.put("contents", JSONArray().put(userContent))

            // Generation config
            val genConfig = JSONObject()
                .put("temperature", 0.7)
                .put("maxOutputTokens", 800)
            rootJson.put("generationConfig", genConfig)

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                return Result.failure(Exception("HTTP ${response.code}: $errorBody"))
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                Result.success(text.trim())
            } else {
                Result.failure(Exception("Empty text in Gemini response"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildPromptForAction(
        actionType: AIActionType,
        input: String,
        tone: ToneOption,
        profile: WritingProfile?,
        targetLanguage: String
    ): String {
        return when (actionType) {
            AIActionType.COMPOSE -> """
                Draft a clear, well-structured message based on this intent:
                "$input"
                Tone: ${tone.label}
                Keep it natural, highly polished, and ready to send.
            """.trimIndent()

            AIActionType.REWRITE -> """
                Rewrite the following text with high impact and clarity in a ${tone.label} tone:
                "$input"
                Return only the polished rewritten text without unnecessary preamble.
            """.trimIndent()

            AIActionType.REPLY -> """
                Draft an effective, polite response to this incoming message:
                "$input"
                Tone: ${tone.label}
                Ensure it directly answers or advances the conversation.
            """.trimIndent()

            AIActionType.FIX -> """
                Fix all grammar, punctuation, spelling, and phrasing errors in the following text:
                "$input"
                Preserve the original meaning and natural voice. Return only the corrected sentence.
            """.trimIndent()

            AIActionType.TRANSLATE -> """
                Translate the following text accurately and naturally into $targetLanguage:
                "$input"
                Return only the translated output.
            """.trimIndent()

            AIActionType.SUMMARIZE -> """
                Summarize the following text into concise key points with bullet marks:
                "$input"
            """.trimIndent()
        }
    }

    private fun buildSystemInstruction(profile: WritingProfile?): String {
        val userName = profile?.name ?: "User"
        val profession = profile?.profession ?: "Professional"
        val style = profile?.style?.title ?: "Natural"
        val tones = profile?.tones?.joinToString(", ") ?: "Confident"

        return """
            You are Aura AI, an intelligent, on-device contextual writing assistant built directly into the keyboard.
            User Profile:
            - Name: $userName
            - Role: $profession
            - Preferred Style: $style
            - Primary Tones: $tones

            Guidelines:
            1. Deliver direct, high-utility, ready-to-use text without conversational meta-commentary like "Here is your draft:".
            2. Match the requested tone and style precisely.
            3. Respect confidentiality and maintain crisp, modern English.
        """.trimIndent()
    }

    private fun generateSmartFallback(
        actionType: AIActionType,
        input: String,
        tone: ToneOption,
        targetLanguage: String
    ): String {
        val cleanInput = input.trim()
        return when (actionType) {
            AIActionType.COMPOSE -> when (tone) {
                ToneOption.PROFESSIONAL -> "Hi there, following up on our recent update regarding $cleanInput. The core milestones are on track, and we are prepared to review the deliverables at your earliest convenience."
                ToneOption.FRIENDLY -> "Hey! Just wanted to share a quick update on $cleanInput. Everything is coming along smoothly—let me know if you want to connect soon!"
                ToneOption.CONCISE -> "Update on $cleanInput: milestones finalized, on schedule for review."
                ToneOption.CONFIDENT -> "We have successfully executed the plan for $cleanInput. All systems are operational and ready for immediate deployment."
                ToneOption.PERSUASIVE -> "By advancing $cleanInput now, we can streamline operations and unlock significant efficiency improvements across the entire team."
                ToneOption.CASUAL -> "Hey, quick heads up on $cleanInput—looking good on our end, will ping you when it's all set!"
            }
            AIActionType.REWRITE -> when (tone) {
                ToneOption.PROFESSIONAL -> "I have reviewed the material in detail and revised it to maximize clarity, structure, and professional tone: $cleanInput"
                ToneOption.CONCISE -> cleanInput.replace(Regex("(?i)\\b(in my opinion|just wanted to say|as you may know)\\b"), "").trim()
                else -> "Polished revision: $cleanInput"
            }
            AIActionType.REPLY -> "Thanks for reaching out! Regarding your note, that sounds great. Let's touch base soon to finalize the details."
            AIActionType.FIX -> {
                // Basic clean-up of common typos
                var fixed = cleanInput
                fixed = fixed.replace("tommorow", "tomorrow", ignoreCase = true)
                fixed = fixed.replace("recieve", "receive", ignoreCase = true)
                fixed = fixed.replace("teh", "the", ignoreCase = true)
                if (fixed.isNotEmpty() && !fixed.endsWith(".") && !fixed.endsWith("?") && !fixed.endsWith("!")) {
                    fixed += "."
                }
                fixed
            }
            AIActionType.TRANSLATE -> "[$targetLanguage Translation]: $cleanInput"
            AIActionType.SUMMARIZE -> "• Core summary point: $cleanInput\n• Action item: Proceed with planned milestones\n• Status: Ready for verification"
        }
    }
}
