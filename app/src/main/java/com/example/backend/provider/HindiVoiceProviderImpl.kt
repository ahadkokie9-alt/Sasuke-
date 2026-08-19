package com.example.backend.provider

import com.example.backend.security.SecretKeyResolver
import com.example.model.AnimeLanguage
import com.example.model.HindiCharacterVoiceConfig
import com.example.model.NarratorVoiceGender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HindiVoiceProviderImpl(
    private val secretKeyResolver: SecretKeyResolver
) : HindiVoiceSynthesisProvider {

    override fun getProviderName(): String = "Neural Narration & Anime Voice Engine"

    override fun checkConfiguration(): ProviderConfigStatus {
        val ttsKey = secretKeyResolver.getTtsApiKey()
        val provider = secretKeyResolver.getTtsProvider()
        return if (provider != "system-tts" && ttsKey.isNullOrBlank()) {
            ProviderConfigStatus.Unconfigured(
                providerName = provider,
                missingKeyName = "TTS_API_KEY",
                instructions = "To connect external cloud TTS ($provider), configure TTS_API_KEY in the Secrets panel or .env file."
            )
        } else {
            ProviderConfigStatus.Ready
        }
    }

    override suspend fun synthesizeNarrator(
        text: String,
        language: AnimeLanguage,
        gender: NarratorVoiceGender,
        pitch: Float,
        speed: Float
    ): HindiVoiceJobResult = withContext(Dispatchers.IO) {
        val fallbackText = if (language == AnimeLanguage.HINDI) {
            "अंधेरे की गहराइयों में, एक नई ताक़त का जन्म हो रहा था..."
        } else {
            "In the depths of darkness, a legendary power was awakening..."
        }
        val formattedSpeech = text.ifBlank { fallbackText }
        val estimatedDuration = (formattedSpeech.length / 14.0f * (1.0f / speed)).coerceAtLeast(3.0f)

        HindiVoiceJobResult(
            audioUrl = "https://cdn.animevideo.ai/audio/narrator_${language.id}_${gender.id}.mp3",
            speakerType = "Narrator (${gender.displayName}, ${language.displayName})",
            durationSeconds = estimatedDuration,
            textSpoken = formattedSpeech
        )
    }

    override suspend fun synthesizeCharacter(
        character: HindiCharacterVoiceConfig,
        dialogue: String
    ): HindiVoiceJobResult = withContext(Dispatchers.IO) {
        val formattedDialogue = dialogue.ifBlank { "मैं कभी हार नहीं मानूँगा!" }
        val estimatedDuration = (formattedDialogue.length / 14.0f * (1.0f / character.speed)).coerceAtLeast(2.5f)

        HindiVoiceJobResult(
            audioUrl = "https://cdn.animevideo.ai/audio/char_${character.id}.mp3",
            speakerType = "${character.characterName} [${character.role}]",
            durationSeconds = estimatedDuration,
            textSpoken = formattedDialogue
        )
    }
}
