package com.example.backend.provider

import com.example.backend.api.ApiClientFactory
import com.example.backend.model.ReplicatePredictionRequest
import com.example.backend.model.SupportedAnimeVideoModels
import com.example.backend.security.SecretKeyResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReplicateVideoProvider(
    private val secretKeyResolver: SecretKeyResolver
) : VideoProvider {

    override fun getProviderName(): String = "Replicate AI Video Engine"

    override fun checkConfiguration(): ProviderConfigStatus {
        val apiKey = secretKeyResolver.getVideoApiKey()
        return if (apiKey.isNullOrBlank()) {
            ProviderConfigStatus.Unconfigured(
                providerName = "Replicate Video API",
                missingKeyName = "VIDEO_API_KEY / REPLICATE_API_TOKEN",
                instructions = "To enable live cloud AI video rendering on Replicate GPU clusters (Wan 2.1, Minimax, CogVideoX, AnimateDiff), add VIDEO_API_KEY (or REPLICATE_API_TOKEN) in the AI Studio Secrets panel or .env file."
            )
        } else {
            ProviderConfigStatus.Ready
        }
    }

    override suspend fun createGeneration(request: VideoGenerationJobRequest): VideoJobResult = withContext(Dispatchers.IO) {
        val apiKey = secretKeyResolver.getVideoApiKey()
        if (apiKey.isNullOrBlank()) {
            return@withContext VideoJobResult.Failed(
                reason = "VIDEO_API_KEY is not configured in Secrets panel or .env. The full-stack pipeline is ready to generate once the key is provided.",
                isConfigError = true
            )
        }

        try {
            // Build enhanced anime prompt with style, duration, lighting, and camera directives
            val enhancedPrompt = buildAnimePrompt(request)
            val configuredModel = secretKeyResolver.getVideoModel()

            val modelDescriptor = SupportedAnimeVideoModels.find { it.id == request.selectedModelId || it.id == configuredModel }
                ?: SupportedAnimeVideoModels.first()

            val isMiniMax = modelDescriptor.replicateModelOwner == "minimax" || modelDescriptor.id == "minimax_video_01"

            val inputPayload: Map<String, Any> = if (isMiniMax) {
                mapOf(
                    "prompt" to enhancedPrompt,
                    "prompt_optimizer" to true
                )
            } else {
                mapOf(
                    "prompt" to enhancedPrompt,
                    "negative_prompt" to "blurry, low quality, deformed anatomy, extra limbs, bad eyes, jitter, flicker, low resolution, realistic photo, western comic style, text watermarks",
                    "num_frames" to (request.durationSeconds * 8).coerceIn(40, 160),
                    "fps" to modelDescriptor.recommendedFps,
                    "aspect_ratio" to request.aspectRatio,
                    "guidance_scale" to 7.5
                )
            }

            val apiRequest = ReplicatePredictionRequest(
                version = null,
                input = inputPayload
            )

            val authHeader = formatAuthHeader(apiKey)
            val response = ApiClientFactory.replicateApi.createModelPrediction(
                authHeader = authHeader,
                modelOwner = modelDescriptor.replicateModelOwner,
                modelName = modelDescriptor.replicateModelName,
                request = apiRequest
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val videoUrl = parseOutputUrl(body.output)
                if (body.status.lowercase() == "succeeded" && videoUrl != null) {
                    VideoJobResult.Success(
                        predictionId = body.id,
                        status = body.status,
                        videoUrl = videoUrl
                    )
                } else {
                    VideoJobResult.InProgress(
                        predictionId = body.id,
                        progressPercent = 10,
                        logMessage = "Job created on Replicate GPU cluster (ID: ${body.id}). Initializing ${modelDescriptor.name}..."
                    )
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "HTTP ${response.code()}: ${response.message()}"
                val detailedMessage = if (response.code() == 401) {
                    "Replicate Authentication Error (401): Token was rejected. Ensure your token from replicate.com/account/api-tokens is entered in the in-app API Settings tab or AI Studio Secrets."
                } else {
                    "Replicate API Error (${response.code()}): $errorMsg"
                }
                VideoJobResult.Failed(detailedMessage)
            }
        } catch (e: Exception) {
            VideoJobResult.Failed("Provider connection error: ${e.localizedMessage ?: e.message}")
        }
    }

    override suspend fun getGenerationStatus(jobId: String): VideoJobResult = withContext(Dispatchers.IO) {
        val apiKey = secretKeyResolver.getVideoApiKey()
        if (apiKey.isNullOrBlank()) {
            return@withContext VideoJobResult.Failed(
                reason = "VIDEO_API_KEY is missing during status polling.",
                isConfigError = true
            )
        }

        try {
            val response = ApiClientFactory.replicateApi.getPredictionStatus(
                authHeader = formatAuthHeader(apiKey),
                predictionId = jobId
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                when (body.status.lowercase()) {
                    "succeeded" -> {
                        val videoUrl = parseOutputUrl(body.output)
                        VideoJobResult.Success(
                            predictionId = body.id,
                            status = "succeeded",
                            videoUrl = videoUrl
                        )
                    }
                    "starting" -> {
                        VideoJobResult.InProgress(body.id, 20, "Allocating GPU instance and initializing model weights...")
                    }
                    "processing" -> {
                        VideoJobResult.InProgress(body.id, 55, "Generating anime frames with neural shaders...")
                    }
                    "failed" -> {
                        VideoJobResult.Failed("Prediction failed: ${body.error ?: "Unknown error"}")
                    }
                    "canceled" -> {
                        VideoJobResult.Failed("Prediction was canceled.")
                    }
                    else -> {
                        VideoJobResult.InProgress(body.id, 35, "Status: ${body.status}")
                    }
                }
            } else {
                VideoJobResult.Failed("Polling status error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            VideoJobResult.Failed("Failed to poll provider status: ${e.localizedMessage ?: e.message}")
        }
    }

    override suspend fun getVideoResult(jobId: String): String? = withContext(Dispatchers.IO) {
        val status = getGenerationStatus(jobId)
        if (status is VideoJobResult.Success) {
            status.videoUrl
        } else {
            null
        }
    }

    override suspend fun cancelPrediction(jobId: String): Boolean = withContext(Dispatchers.IO) {
        val apiKey = secretKeyResolver.getVideoApiKey() ?: return@withContext false
        try {
            val response = ApiClientFactory.replicateApi.cancelPrediction(
                authHeader = formatAuthHeader(apiKey),
                predictionId = jobId
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    private fun formatAuthHeader(key: String): String {
        val trimmed = key.trim()
        return if (trimmed.startsWith("Bearer ", ignoreCase = true) || trimmed.startsWith("Token ", ignoreCase = true)) {
            trimmed
        } else {
            "Bearer $trimmed"
        }
    }

    private fun buildAnimePrompt(request: VideoGenerationJobRequest): String {
        val styleTag = request.visualStyle.promptTag
        val consistencyTag = if (request.characterConsistency) {
            "locked character appearance, exact same anime hairstyle, consistent costume design and outfit colors across cuts, distinct facial features"
        } else ""
        val continuityTag = if (request.sceneContinuity) {
            "sequential anime narrative continuity, cinematic camera tracking, fluid motion transitions"
        } else ""

        return "${request.storyPrompt}, $styleTag, $consistencyTag, $continuityTag, aspect ratio ${request.aspectRatio}, duration ${request.durationSeconds}s, language ${request.language.displayName}, high quality anime production"
    }

    private fun parseOutputUrl(output: Any?): String? {
        if (output == null) return null
        return when (output) {
            is String -> output
            is List<*> -> output.firstOrNull()?.toString()
            else -> output.toString()
        }
    }
}
