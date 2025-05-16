package com.example.appv1.api

import retrofit2.http.Body
import retrofit2.http.POST

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

data class MistralChatChoice(
    val message: ChatMessage
)

data class ChatResponse(
    val content: String,
)

interface MistralApi {
    @POST("/mistral")
    suspend fun generateChatCompletion(@Body request: MistralChatRequest): ChatResponse // <-- Utilisation des nouvelles classes
}