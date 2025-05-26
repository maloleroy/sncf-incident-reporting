package com.example.appv1.api

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

fun getIncidentAnalysis(
    incidentAnalysisRequest: IncidentAnalysisRequest,
    context: Context,
    onResult: (IncidentAnalysisResponse) -> Unit,
    onError: (String) -> Unit,
) {
    if (incidentAnalysisRequest.trainType.isBlank()) {
        onError("Veuillez préciser le train")
        return
    }
    if (incidentAnalysisRequest.trainCar.isBlank()) {
        onError("Veuillez préciser le numéro de rame")
        return
    }
    if (incidentAnalysisRequest.transcription.isBlank()) {
        onError("Veuillez préciser la description de l'incident")
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.getIncidentApiService(context)
                .generateIncidentAnalysis(incidentAnalysisRequest)
            withContext(Dispatchers.Main) {
                onResult(response)
            }
        } catch (e: retrofit2.HttpException) {
            withContext(Dispatchers.Main) {
                when (e.code()) {
                    404 -> onError("Aucun incident trouvé")
                    500 -> onError("Erreur serveur")
                    else -> onError("Erreur HTTP ${e.code()}")
                }
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                onError("Erreur de réseau lors de l'appel API : ${e.message}")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Erreur inattendue lors de l'appel API : ${e.message}")
            }
        }
    }
}


fun getCompletion(
    informations: IncidentCompletionRequest,
    context: Context,
    onResult: (IncidentCompletionResponse) -> Unit,
    onError: (String) -> Unit,
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.getCompletionApiService(context)
                .findCompletion(informations)
            withContext(Dispatchers.Main) {
                onResult(response)
            }
        } catch (e: retrofit2.HttpException) {
            withContext(Dispatchers.Main) {
                when (e.code()) {
                    404 -> onError("Aucun incident trouvé")
                    500 -> onError("Erreur serveur")
                    else -> onError("Erreur HTTP ${e.code()}")
                }
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                onError("Erreur de réseau lors de l'appel API : ${e.message}")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Erreur inattendue lors de l'appel API : ${e.message}")
            }
        }
    }
}

fun loadOptionsForLevel(
    level: String,
    selections: ConservedInformations,
    context: Context,
    onSuccess: (List<String>) -> Unit,
    onError: (String) -> Unit
) {
    val request = IncidentCompletionRequest(level, selections)
    CoroutineScope(Dispatchers.IO).launch {
        try {
            
            val response = RetrofitInstance.getCompletionApiService(context).findCompletion(request)
            withContext(Dispatchers.Main) {
                onSuccess(response.options)
            }
        } catch (e: retrofit2.HttpException) {
            withContext(Dispatchers.Main) {
                onError("Erreur HTTP ${e.code()}")
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                onError("Erreur réseau : ${e.message}")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Erreur inattendue : ${e.message}")
            }
        }
    }
}



fun submitIncident(
    incident: IncidentAnalysisResponse,
    context: Context,
    onResult: (IncidentSubmittingResponse) -> Unit,
    onError: (String) -> Unit,
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.getIncidentApiService(context)
                .submitIncident(incident)
            withContext(Dispatchers.Main) {
                onResult(response)
            }
        } catch (e: retrofit2.HttpException) {
            withContext(Dispatchers.Main) {
                when (e.code()) {
                    404 -> onError("Aucun incident trouvé")
                    500 -> onError("Erreur serveur")
                    else -> onError("Erreur HTTP ${e.code()}")
                }
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                onError("Erreur de réseau lors de l'appel API : ${e.message}")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Erreur inattendue lors de l'appel API : ${e.message}")
            }
        }
    }
}
