package com.example.data.api

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String // Base64
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Double? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

class GeminiManager {
    private val apiKey = BuildConfig.GEMINI_API_KEY

    suspend fun askSensei(history: List<com.example.data.model.ChatMessage>, currentPrompt: String): String {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Sensei: Welcome, Disciple. Please set up your GEMINI_API_KEY in the Secrets panel so I can provide customized martial training, philosophical insights, and nutritional advice."
        }

        val requestContents = mutableListOf<Content>()

        // System Instruction to set persona
        val systemInstructionText = """
            You are Sensei, an elite world-class martial arts grandmaster (possessing deep expertise in MMA, Boxing, Kickboxing, Muay Thai, Wrestling, BJJ, and Kyokushin Karate) and an inspiring self-improvement, mental toughness, and discipline coach.
            Your tone is firm yet profoundly encouraging, disciplined, philosophical, and energetic—like a premium advisor meets a martial arts mentor.
            You help users structure custom workout plans, answer questions about technique, analyze homework/discipline hurdles, or suggest daily challenges. Keep answers practical, structured with bold labels, and highly motivating.
        """.trimIndent()

        // Map conversational history
        history.forEach { msg ->
            val role = if (msg.sender == "user") "user" else "model"
            requestContents.add(Content(parts = listOf(Part(text = msg.text))))
        }

        // Add the current prompt
        requestContents.add(Content(parts = listOf(Part(text = currentPrompt))))

        val request = GenerateContentRequest(
            contents = requestContents,
            generationConfig = GenerationConfig(temperature = 0.7),
            systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)))
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Sensei is currently meditating. Please try again in a moment."
        } catch (e: Exception) {
            Log.e("GeminiManager", "Error calling Gemini API", e)
            "Sensei: I hit a brief wall in communications, Disciple. Let us refocus. (Error: ${e.message})"
        }
    }

    suspend fun analyzeFoodImage(bitmap: Bitmap, prompt: String): String {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Sensei Food Cam: [DEMO MODE - Set your GEMINI_API_KEY in Secrets for real-time AI scans]\n\nBased on your image, it looks like a high-protein discipline bowl.\nEstimated Calories: 450 kcal\nProtein: 35g\nCarbs: 40g\nFat: 15g\n\nEating clean is the fuel of conviction!"
        }

        val base64Data = try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            return "Sensei Food Cam Error: Could not process image. Please try again."
        }

        val systemInstructionText = """
            You are 'Conviction Nutrition Cam AI'. Given a photo of food and a text description, you must identify the food and estimate its dietary macronutrients (Calories, Protein in grams, Carbs in grams, Fat in grams).
            Return a structured, easy-to-read list including specific, actionable health suggestions. Use bold headers. Keep it tight and professional.
            Provide estimates even if tentative, in a standard structured form like:
            * Food Identified: ...
            * Estimated Calories: ...
            * Protein: ...
            * Carbs: ...
            * Fat: ...
            * Sensei's Nutritional Verdict: [1-2 sentences of martial arts fitness context]
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Data))
                    )
                )
            ),
            generationConfig = GenerationConfig(temperature = 0.4),
            systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)))
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No response from AI Food Cam. Refocus your camera."
        } catch (e: Exception) {
            Log.e("GeminiManager", "Error analyzing food image", e)
            "Sensei Food Cam: I failed to look closely. Let us try logging again. (Error: ${e.message})"
        }
    }
}
