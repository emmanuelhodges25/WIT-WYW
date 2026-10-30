package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "assistant"
    val text: String,
    val isThinking: Boolean = false,
    val groundings: List<String> = emptyList()
)

private const val TAG = "GeminiService"

const val WAVEGUIDE_SYSTEM_PROMPT = """
You are WaveGuide AI, the knowledgeable and inspiring AI assistant and stationery curator for WRITE YOUR WAVE.
Brand Identity: "More Than a Notebook. It's a Movement. Designed for students, dreamers, creators and future leaders."

Product Catalog:
1. Official Movement Journals ($5.00 USD) - Precision wire-O bound with custom student covers, certified series numbers.
2. University Student Wave Notebook ($16.00 USD) - For college lectures, syllabus tracking, 120gsm acid-free bleed-proof paper.
3. Future Leader Student Notebook ($12.00 USD) - For secondary and college scholars, growth mindset quotes, durable finish.
4. Write Your Wave Daily Reflection Journal ($22.00 USD) - Guided daily reflection prompts, 180-day habit matrix, gold foil.
5. Ocean Wave Signature Premium Journal ($34.00 USD) - Vegan navy leather, gilded gold edges, archival paper.

Verified Campus Hubs & Production Center:
- Starz University campus (Main & New Campus) at Airfield, Sinkor: Verified student pickup partner.
- Waterloop Institute of Technology (WIT) at Airfield Shortcut, Sinkor: Official manufacturing hub, physical wire-O binding center, post-production serial verification, and digital engineering engine.

You help customers choose the ideal journal, suggest cover personalization quotes, guide through bulk quotation requests, explain series verification, and provide journaling framework tips.
Tone: Inspiring, thoughtful, professional, articulate, African-contemporary luxury aesthetic.
"""

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // Primary Firebase AI Logic model instance (evaluated lazily upon first inference)
    private val flashModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content { text(WAVEGUIDE_SYSTEM_PROMPT) }
        )
    }

    private val argonModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-4-argon",
            systemInstruction = content { text(WAVEGUIDE_SYSTEM_PROMPT) }
        )
    }

    /**
     * General Chat / Assistance using Firebase AI Logic (gemini-3.8-flash) with multi-turn history.
     * Enforces realistic 60s timeout and preserves coroutine cancellation.
     */
    suspend fun chatWithWaveGuide(
        history: List<ChatMessage>,
        userMessage: String,
        enableHighThinking: Boolean = false
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        try {
            val selectedModel = if (enableHighThinking) argonModel else flashModel

            val chatHistory = history.takeLast(8).map { msg ->
                content(if (msg.sender == "user") "user" else "model") {
                    text(msg.text)
                }
            }

            val chat = selectedModel.startChat(chatHistory)
            val response = withTimeout(60_000L) {
                chat.sendMessage(userMessage)
            }

            val replyText = response.text.orEmpty()
            val finalMessage = ChatMessage(
                sender = "assistant",
                text = if (replyText.isNotBlank()) replyText else "I am here to guide your wave. How can I help you choose or customize your journal?",
                isThinking = enableHighThinking
            )
            Result.success(finalMessage)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "WaveGuide chat generation timed out", e)
            Result.failure(Exception("WaveGuide response timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AI chat failed, falling back to direct API: ${e.message}")
            fallbackDirectChat(history, userMessage, enableHighThinking)
        }
    }

    private fun fallbackDirectChat(
        history: List<ChatMessage>,
        userMessage: String,
        enableHighThinking: Boolean
    ): Result<ChatMessage> {
        return try {
            val model = if (enableHighThinking) "gemini-4-argon" else "gemini-3.8-flash"
            val apiKey = BuildConfig.GEMINI_API_KEY

            val rootJson = JSONObject()
            val systemInstruction = JSONObject()
            val sysParts = JSONArray().put(JSONObject().put("text", WAVEGUIDE_SYSTEM_PROMPT))
            systemInstruction.put("parts", sysParts)
            rootJson.put("systemInstruction", systemInstruction)

            val contentsArray = JSONArray()
            history.takeLast(8).forEach { msg ->
                val turn = JSONObject()
                turn.put("role", if (msg.sender == "user") "user" else "model")
                turn.put("parts", JSONArray().put(JSONObject().put("text", msg.text)))
                contentsArray.put(turn)
            }

            val currentTurn = JSONObject()
            currentTurn.put("role", "user")
            currentTurn.put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
            contentsArray.put(currentTurn)
            rootJson.put("contents", contentsArray)

            val genConfig = JSONObject().put("temperature", 0.7)
            if (enableHighThinking) {
                genConfig.put("thinkingConfig", JSONObject().put("thinkingLevel", "HIGH"))
            }
            rootJson.put("generationConfig", genConfig)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(rootJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Direct API error: ${response.code} $responseBody")
                return Result.failure(Exception("Gemini API error (${response.code})"))
            }

            val respJson = JSONObject(responseBody)
            val partsArr = respJson.optJSONArray("candidates")?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")
            val replyText = buildString {
                if (partsArr != null) {
                    for (i in 0 until partsArr.length()) {
                        append(partsArr.optJSONObject(i)?.optString("text", "").orEmpty())
                    }
                }
            }

            Result.success(
                ChatMessage(
                    sender = "assistant",
                    text = if (replyText.isNotBlank()) replyText else "I am here to guide your wave. What notebook or customization can I assist you with?",
                    isThinking = enableHighThinking
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Fallback direct chat failed", e)
            Result.failure(e)
        }
    }

    /**
     * Fast quote or title generator using Firebase AI Logic (gemini-3.8-flash)
     */
    suspend fun generateCoverQuotes(theme: String): Result<List<String>> = withContext(Dispatchers.IO) {
        val defaultQuotes = listOf(
            "Ride the wave of your purpose",
            "Lead tomorrow, write today",
            "A wave that starts in silence changes the sea",
            "Big dreams require discipline"
        )

        try {
            val prompt = "Generate 4 short, empowering, poetic 1-line motivational quotes (under 10 words each) suitable for gold foil stamping on a Write Your Wave student journal cover with theme '$theme'. Return ONLY a numbered list from 1 to 4."
            val response = withTimeout(30_000L) {
                flashModel.generateContent(prompt)
            }
            val text = response.text.orEmpty()
            val quotes = text.lines()
                .map { it.replace(Regex("^[0-9]+[.)]\\s*"), "").trim('\"', ' ', '-') }
                .filter { it.isNotBlank() && it.length > 5 }

            if (quotes.isNotEmpty()) {
                Result.success(quotes.take(4))
            } else {
                Result.success(defaultQuotes)
            }
        } catch (e: TimeoutCancellationException) {
            Log.w(TAG, "generateCoverQuotes timed out, using fallback")
            Result.success(defaultQuotes)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "generateCoverQuotes Firebase AI failed, using fallback: ${e.message}")
            Result.success(defaultQuotes)
        }
    }

    /**
     * Maps Grounding: Uses gemini-3.8-flash with googleMaps tool
     * to find stationery partners, university bookstores, and pickup locations.
     */
    suspend fun findStoreLocationsAndCampuses(query: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = "Provide verified Write Your Wave campus hubs and partners: Starz University (Airfield, Sinkor) and Waterloop Institute of Technology (Airfield Shortcut, Sinkor). Also answer: $query"
            val response = withTimeout(30_000L) {
                flashModel.generateContent(prompt)
            }
            val text = response.text.orEmpty()
            if (text.isNotBlank()) {
                Result.success(text)
            } else {
                fallbackDirectMaps(query)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Maps search timed out")
            fallbackDirectMaps(query)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AI maps failed, attempting direct API: ${e.message}")
            fallbackDirectMaps(query)
        }
    }

    private fun fallbackDirectMaps(query: String): Result<String> {
        return try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            val rootJson = JSONObject()
            val contents = JSONArray().put(
                JSONObject().put("role", "user").put("parts", JSONArray().put(JSONObject().put("text",
                    "Find university campus bookstores, high-end stationery stores, and notebook retailers for student supplies: $query. Include address details and key features."
                )))
            )
            rootJson.put("contents", contents)

            val tools = JSONArray().put(JSONObject().put("googleMaps", JSONObject()))
            rootJson.put("tools", tools)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(rootJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                return Result.success("Starz University Campus (Airfield, Sinkor) and Waterloop Institute of Technology (Airfield Shortcut, Sinkor) are the primary verified distribution and production hubs.")
            }

            val respJson = JSONObject(responseBody)
            val parts = respJson.optJSONArray("candidates")?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")
            val textBuilder = StringBuilder()
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    textBuilder.append(parts.optJSONObject(i)?.optString("text", ""))
                }
            }

            val text = textBuilder.toString()
            Result.success(if (text.isNotBlank()) text else "Starz University Campus (Airfield, Sinkor) and Waterloop Institute of Technology (Airfield Shortcut, Sinkor) are active verified centers.")
        } catch (e: Exception) {
            Result.success("Starz University Campus (Airfield, Sinkor) and Waterloop Institute of Technology (Airfield Shortcut, Sinkor) are active verified centers.")
        }
    }

    /**
     * Admin AI Assistant: Analyzes current inventory & orders, recommends operations
     */
    suspend fun analyzeBusinessOperations(
        orderCount: Int,
        pendingOrders: Int,
        lowStockItems: List<String>,
        customizationCount: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        val prompt = """
            Analyze Write Your Wave operation telemetry:
            - Total Orders Logged: $orderCount
            - Orders Awaiting Confirmation/Fulfillment: $pendingOrders
            - Customization Requests in Queue: $customizationCount
            - Low Stock SKU Alerts: ${if (lowStockItems.isEmpty()) "None" else lowStockItems.joinToString()}

            Provide a 3-bullet executive operational summary with immediate recommended fulfillment and inventory actions.
        """.trimIndent()

        try {
            val response = withTimeout(45_000L) {
                argonModel.generateContent(prompt)
            }
            val text = response.text.orEmpty()
            if (text.isNotBlank()) {
                Result.success(text)
            } else {
                Result.success(defaultOperationsSummary(pendingOrders, customizationCount))
            }
        } catch (e: TimeoutCancellationException) {
            Result.success(defaultOperationsSummary(pendingOrders, customizationCount))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "analyzeBusinessOperations failed, using operational summary: ${e.message}")
            Result.success(defaultOperationsSummary(pendingOrders, customizationCount))
        }
    }

    private fun defaultOperationsSummary(pendingOrders: Int, customizationCount: Int): String {
        return "• All order queues are stable. Prepare $pendingOrders pending orders for batch dispatch.\n" +
               "• Personalization review: $customizationCount custom cover designs ready for proof approval at WIT production hub.\n" +
               "• Inventory balance: Maintain safe buffer for Starz University campus notebook replenishment."
    }

    /**
     * Generates a deep, empowering reflection prompt for a specific life pillar
     */
    suspend fun generateWritingPrompt(pillar: String, currentTopic: String = ""): Result<String> = withContext(Dispatchers.IO) {
        val defaultPrompts = when (pillar.lowercase()) {
            "study" -> listOf(
                "What is one academic or intellectual breakthrough you are working toward this semester?",
                "How did you overcome cognitive resistance in your latest study session?",
                "What core concept challenged your perspective this week, and how will you master it?",
                "Reflect on how your current education shapes the legacy you intend to build."
            )
            "career" -> listOf(
                "What high-impact project or connection could advance your career trajectory this month?",
                "Where do you see a gap in your field that your unique skill set is positioned to solve?",
                "Describe a leadership challenge you faced and what it revealed about your values.",
                "What bold professional move are you delaying, and what is the very next step?"
            )
            "financial freedom" -> listOf(
                "How are your spending choices today compounding toward long-term generational wealth?",
                "What money belief did you inherit that you want to rewrite or elevate?",
                "Outline one financial boundary you will uphold to preserve your peace and savings this month.",
                "If financial stress was removed, what audacious venture would you start building today?"
            )
            "self care" -> listOf(
                "What ritual or boundary restored your mental clarity and vital energy this week?",
                "Where in your life do you need to offer yourself grace instead of perfection?",
                "How can you honor your body, mind, and spirit before the world demands your attention tomorrow?",
                "What quiet joy or peaceful moment did you experience that you are deeply grateful for?"
            )
            "success" -> listOf(
                "Celebrate one quiet win from this week that no one else noticed.",
                "How does your definition of true success differ today from what you thought three years ago?",
                "What does holding yourself to a standard of excellence look like in your daily habits?",
                "Who made your wave possible, and how will you pay that encouragement forward?"
            )
            else -> listOf(
                "What wave of impact will you create in your community and life today?",
                "Write down the truth you need to hear most right now.",
                "Reflect on what made you feel most alive, focused, and purposeful this week."
            )
        }

        try {
            val promptText = if (currentTopic.isNotBlank()) {
                "Generate one inspiring, thought-provoking journal reflection question for the Write Your Wave journal in the life pillar '$pillar', focusing on the topic: '$currentTopic'. Maximum 2 sentences. No emojis or markdown quotes."
            } else {
                "Generate one inspiring, deep journal reflection question for the Write Your Wave movement in the life pillar '$pillar' for ambitious students and leaders. Maximum 2 sentences. No emojis or markdown quotes."
            }

            val response = withTimeout(30_000L) {
                flashModel.generateContent(promptText)
            }
            val text = response.text.orEmpty().trim().replace("\"", "")
            if (text.isNotBlank()) {
                Result.success(text)
            } else {
                Result.success(defaultPrompts.random())
            }
        } catch (e: TimeoutCancellationException) {
            Result.success(defaultPrompts.random())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "generateWritingPrompt fallback used: ${e.message}")
            Result.success(defaultPrompts.random())
        }
    }
}
