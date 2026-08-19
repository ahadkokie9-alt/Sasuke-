package com.example.backend.provider

import com.example.model.AnimeLanguage
import com.example.model.HindiCharacterVoiceConfig
import com.example.model.NarratorVoiceGender
import com.example.model.StorySceneSpec
import com.example.model.VisualAnimeStyle

/**
 * Common request payload for real video generation backend
 */
data class VideoGenerationJobRequest(
    val storyPrompt: String,
    val visualStyle: VisualAnimeStyle,
    val durationSeconds: Int = 10,
    val aspectRatio: String = "9:16",
    val language: AnimeLanguage = AnimeLanguage.HINDI,
    val narratorGender: NarratorVoiceGender = NarratorVoiceGender.MALE,
    val bgmEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val characterConsistency: Boolean = true,
    val sceneContinuity: Boolean = true,
    val cameraMovement: String = "dynamic_pan",
    val lightingEffect: String = "sakura_bloom",
    val smoothTransitions: Boolean = true,
    val selectedModelId: String = "wan-video/wan-2.1-t2v-480p"
)

sealed class ProviderConfigStatus {
    object Ready : ProviderConfigStatus()
    data class Unconfigured(
        val providerName: String,
        val missingKeyName: String,
        val instructions: String
    ) : ProviderConfigStatus()
}

sealed class VideoJobResult {
    data class Success(val predictionId: String, val status: String, val videoUrl: String? = null) : VideoJobResult()
    data class InProgress(val predictionId: String, val progressPercent: Int, val logMessage: String) : VideoJobResult()
    data class Failed(val reason: String, val isConfigError: Boolean = false) : VideoJobResult()
}

data class SceneGenerationResult(
    val sceneIndex: Int,
    val clipUrl: String,
    val durationSeconds: Int,
    val promptUsed: String
)

data class HindiVoiceJobResult(
    val audioUrl: String,
    val speakerType: String,
    val durationSeconds: Float,
    val textSpoken: String
)

/**
 * VideoProvider Abstraction Interface
 * Enables plugging in Replicate, Fal, Minimax, CogVideoX, Wan2.1, or custom providers
 * without modifying the rest of the application.
 */
interface VideoProvider {
    fun getProviderName(): String
    fun checkConfiguration(): ProviderConfigStatus
    suspend fun createGeneration(request: VideoGenerationJobRequest): VideoJobResult
    suspend fun getGenerationStatus(jobId: String): VideoJobResult
    suspend fun getVideoResult(jobId: String): String?
    suspend fun cancelPrediction(jobId: String): Boolean
}

/**
 * VideoGenerationProvider alias for backwards compatibility
 */
typealias VideoGenerationProvider = VideoProvider

/**
 * TTS & Voice Synthesis Provider Interface
 */
interface HindiVoiceSynthesisProvider {
    fun getProviderName(): String
    fun checkConfiguration(): ProviderConfigStatus
    suspend fun synthesizeNarrator(
        text: String,
        language: AnimeLanguage,
        gender: NarratorVoiceGender,
        pitch: Float,
        speed: Float
    ): HindiVoiceJobResult
    suspend fun synthesizeCharacter(character: HindiCharacterVoiceConfig, dialogue: String): HindiVoiceJobResult
}

/**
 * Scene Decomposer Provider Interface
 */
interface SceneDecomposerProvider {
    suspend fun decomposeStory(
        storyPrompt: String,
        visualStyle: VisualAnimeStyle,
        durationSeconds: Int,
        language: AnimeLanguage,
        narratorGender: NarratorVoiceGender,
        cameraMovement: String,
        lightingEffect: String,
        characterConsistency: Boolean,
        characters: List<HindiCharacterVoiceConfig>
    ): List<StorySceneSpec>
}

/**
 * Audio Mixer Provider Interface
 */
interface AudioMixerProvider {
    suspend fun mixAudioTracks(
        narratorAudioUrls: List<String>,
        characterAudioUrls: List<String>,
        bgmTrackId: String,
        bgmVolume: Float,
        bgmEnabled: Boolean,
        sfxEnabled: Boolean,
        sfxList: List<String>
    ): String
}

/**
 * Scene Stitcher Provider Interface
 */
interface SceneStitcherProvider {
    suspend fun combineScenes(
        sceneClips: List<SceneGenerationResult>,
        audioTrackUrl: String,
        transitionType: String
    ): String
}
