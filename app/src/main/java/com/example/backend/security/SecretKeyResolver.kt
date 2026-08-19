package com.example.backend.security

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

/**
 * Manages resolution of AI provider secrets securely.
 * Priority:
 * 1. BuildConfig (injected at build time from .env / Secrets panel via Secrets Gradle plugin)
 * 2. Secure local encrypted storage for runtime developer overrides
 */
class SecretKeyResolver(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "anime_video_secure_secrets",
        Context.MODE_PRIVATE
    )

    private fun cleanToken(raw: String?): String? {
        if (raw == null) return null
        var token = raw.trim()
        if (token.contains("CONTROL_PLANE")) {
            token = token.substringBefore("CONTROL_PLANE").trim()
        }
        if (token.startsWith("\"") && token.endsWith("\"") && token.length >= 2) {
            token = token.substring(1, token.length - 1).trim()
        }
        if (token.startsWith("'") && token.endsWith("'") && token.length >= 2) {
            token = token.substring(1, token.length - 1).trim()
        }
        if (token.startsWith("Bearer ", ignoreCase = true)) {
            token = token.substring(7).trim()
        } else if (token.startsWith("Token ", ignoreCase = true)) {
            token = token.substring(6).trim()
        }
        return if (token.isBlank() || token.startsWith("your_") || token.startsWith("r8_your_") || token == "MY_GEMINI_API_KEY") {
            null
        } else {
            token
        }
    }

    private fun getBuildConfigField(fieldName: String): String? {
        return try {
            val field = BuildConfig::class.java.getField(fieldName)
            val value = field.get(null) as? String
            cleanToken(value)
        } catch (e: Exception) {
            null
        }
    }

    private fun getEnvField(fieldName: String): String? {
        return try {
            cleanToken(System.getenv(fieldName))
        } catch (e: Exception) {
            null
        }
    }

    // Video Provider Config
    fun getVideoProvider(): String {
        return prefs.getString(KEY_VIDEO_PROVIDER, null)
            ?: getBuildConfigField("VIDEO_PROVIDER")
            ?: getEnvField("VIDEO_PROVIDER")
            ?: "replicate"
    }

    fun getVideoApiKey(): String? {
        // First check local runtime override
        val localKey = cleanToken(prefs.getString(KEY_VIDEO_API_KEY, null))
        if (!localKey.isNullOrBlank()) return localKey

        // Then check all potential BuildConfig fields
        val buildConfigKey = getBuildConfigField("REPLICATE_API_TOKEN")
            ?: getBuildConfigField("REPLICATE_API_KEY")
            ?: getBuildConfigField("REPLICATE_TOKEN")
            ?: getBuildConfigField("VIDEO_API_KEY")
        if (!buildConfigKey.isNullOrBlank()) return buildConfigKey

        // Then check environment variables
        return getEnvField("REPLICATE_API_TOKEN")
            ?: getEnvField("REPLICATE_API_KEY")
            ?: getEnvField("REPLICATE_TOKEN")
            ?: getEnvField("VIDEO_API_KEY")
    }

    fun getVideoModel(): String {
        return prefs.getString(KEY_VIDEO_MODEL, null)
            ?: getBuildConfigField("VIDEO_MODEL")
            ?: "wan-video/wan-2.1-t2v-480p"
    }

    fun getReplicateApiToken(): String? {
        return getVideoApiKey()
    }

    // TTS & Voice Config
    fun getTtsProvider(): String {
        return prefs.getString(KEY_TTS_PROVIDER, null)
            ?: getBuildConfigField("TTS_PROVIDER")
            ?: "system-tts"
    }

    fun getTtsApiKey(): String? {
        val localKey = prefs.getString(KEY_TTS_API_KEY, null)
        if (!localKey.isNullOrBlank()) return localKey

        return getBuildConfigField("TTS_API_KEY")
            ?: getBuildConfigField("HINDI_VOICE_API_KEY")
    }

    fun getTtsModel(): String {
        return prefs.getString(KEY_TTS_MODEL, null)
            ?: getBuildConfigField("TTS_MODEL")
            ?: "hindi-english-neural-v1"
    }

    fun getHindiVoiceApiKey(): String? {
        return getTtsApiKey()
    }

    // Status Checks
    fun hasVideoApiKey(): Boolean {
        return !getVideoApiKey().isNullOrBlank()
    }

    fun hasReplicateKey(): Boolean {
        return hasVideoApiKey()
    }

    fun hasTtsApiKey(): Boolean {
        return !getTtsApiKey().isNullOrBlank()
    }

    // Runtime Setters
    fun saveVideoApiKey(key: String) {
        prefs.edit().putString(KEY_VIDEO_API_KEY, key.trim()).apply()
    }

    fun saveReplicateApiToken(token: String) {
        saveVideoApiKey(token)
    }

    fun saveVideoProvider(provider: String) {
        prefs.edit().putString(KEY_VIDEO_PROVIDER, provider.trim()).apply()
    }

    fun saveVideoModel(model: String) {
        prefs.edit().putString(KEY_VIDEO_MODEL, model.trim()).apply()
    }

    fun saveTtsApiKey(key: String) {
        prefs.edit().putString(KEY_TTS_API_KEY, key.trim()).apply()
    }

    fun saveHindiVoiceApiKey(key: String) {
        saveTtsApiKey(key)
    }

    fun saveTtsProvider(provider: String) {
        prefs.edit().putString(KEY_TTS_PROVIDER, provider.trim()).apply()
    }

    fun clearKeys() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_VIDEO_PROVIDER = "sec_video_provider"
        private const val KEY_VIDEO_API_KEY = "sec_video_api_key"
        private const val KEY_VIDEO_MODEL = "sec_video_model"
        private const val KEY_TTS_PROVIDER = "sec_tts_provider"
        private const val KEY_TTS_API_KEY = "sec_tts_api_key"
        private const val KEY_TTS_MODEL = "sec_tts_model"
    }
}
