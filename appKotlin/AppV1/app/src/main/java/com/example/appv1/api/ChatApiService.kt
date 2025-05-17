package com.example.appv1.api

import com.example.appv1.BuildConfig
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatMessage(
    val role: String, // "user", "assistant", "system"
    val content: String
)

data class ChatRequest(
    val messages: List<ChatMessage>,
)

data class ChatResponse(
    val content: String,
)

interface ChatApiService {
    @POST(BuildConfig.BACKEND_AI_ROUTE)
    suspend fun generateChatCompletion(@Body request: ChatRequest): ChatResponse // <-- Utilisation des nouvelles classes
}