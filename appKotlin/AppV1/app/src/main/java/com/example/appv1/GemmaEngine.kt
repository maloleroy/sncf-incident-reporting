package com.example.appv1 // Assurez-vous que le package est correct

import android.content.Context
import android.util.Log
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import java.util.concurrent.Executors

class GemmaEngine(context: Context) {

    private var llmInference: LlmInference? = null
    // Executor pour gérer les callbacks de ListenableFuture
    private val backgroundExecutor = Executors.newSingleThreadExecutor()


    init {
        try {
            val options = LlmInferenceOptions.builder()
                // Spécifiez uniquement le nom du fichier dans les assets
                .setModelPath("gemma_2b_it_gpu_int8.tflite") // Assurez-vous que ce nom est correct
                // .setMaxTokens(1024) // Optionnel
                //.setTemperature(0.7f) // setTemperature existe
                // .setTopK(40) // Autre option possible
                .build()
            llmInference = LlmInference.createFromOptions(context, options)
            Log.i("GemmaEngine", "LlmInference initialized.")
        } catch (e: Exception) {
            Log.e("GemmaEngine", "Error initializing LlmInference", e)
        }
    }

    // Fonction synchrone (bloque le thread courant jusqu'à la réponse)
    // À utiliser avec précaution, idéalement pas sur le thread UI
    fun ask(prompt: String): String? {
        if (llmInference == null) {
            Log.e("GemmaEngine", "LlmInference not initialized for ask.")
            return null
        }
        return try {
            // generateResponse retourne directement la String
            llmInference?.generateResponse(prompt)
        } catch (e: Exception) {
            Log.e("GemmaEngine", "Error during sync generation", e)
            null
        }
    }

    // Fonction asynchrone qui retourne la réponse complète via des callbacks
    fun askAsync(
        prompt: String,
        onSuccess: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (llmInference == null) {
            Log.e("GemmaEngine", "LlmInference not initialized for askAsync.")
            onError(IllegalStateException("LlmInference not initialized"))
            return
        }
        try {
            // generateResponseAsync retourne un ListenableFuture<String>
            val resultFuture: ListenableFuture<String> = llmInference!!.generateResponseAsync(prompt)

            // Ajouter un callback pour gérer le résultat du Future
            Futures.addCallback(resultFuture, object : FutureCallback<String> {
                override fun onSuccess(result: String?) {
                    if (result != null) {
                        // Passer le résultat au callback onSuccess fourni
                        onSuccess(result)
                    } else {
                        onError(IllegalStateException("Received null result from LlmInference"))
                    }
                }

                override fun onFailure(t: Throwable) {
                    Log.e("GemmaEngine", "Async generation failed", t)
                    // Passer l'erreur au callback onError fourni
                    onError(t)
                }
            }, backgroundExecutor) // Utiliser l'executor pour le callback

        } catch (e: Exception) {
            Log.e("GemmaEngine", "Error starting async generation", e)
            onError(e)
        }
    }


    fun close() {
        llmInference?.close()
        llmInference = null
        backgroundExecutor.shutdown() // Arrêter l'executor
        Log.i("GemmaEngine", "LlmInference closed.")
    }
}
