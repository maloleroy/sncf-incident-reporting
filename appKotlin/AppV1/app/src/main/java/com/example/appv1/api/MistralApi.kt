package com.example.appv1.api

import retrofit2.http.Body
import retrofit2.http.POST

data class ReportRequest(val text: String)
data class ReportResponse(val success: Boolean, val message: String)

// --- Structures de données pour l'API Mistral Chat Completions ---

// Message dans la conversation
data class ChatMessage(
    val role: String, // "user", "assistant", "system"
    val content: String
)

// Requête pour l'API Chat Completions
data class MistralChatRequest(
    val model: String, // e.g., "mistral-small-latest" or "mistral-large-latest"
    val messages: List<ChatMessage>,
    val temperature: Float? = null, // Optional parameters
    val max_tokens: Int? = null,    // Optional parameters
    val stream: Boolean? = false    // Optional: Set to false for single response
)

// Réponse de l'API Chat Completions (structure simplifiée)
data class MistralChatChoice(
    val message: ChatMessage
)

data class MistralChatResponse(
    val id: String,
    val model: String,
    val choices: List<MistralChatChoice>,
    // Include other fields as needed (e.g., usage statistics)
)

interface MistralApi {
    // Utilisation de l'endpoint officiel et des nouvelles data classes
    @POST("v1/chat/completions") // <-- Endpoint officiel
    suspend fun generateChatCompletion(@Body request: MistralChatRequest): MistralChatResponse // <-- Utilisation des nouvelles classes
}