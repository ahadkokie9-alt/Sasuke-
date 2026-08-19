package com.example.model

/**
 * Visual Style Options requested:
 * - 2D Anime
 * - 3D Anime
 * - Premium Anime
 * - Cinematic Anime
 */
enum class VisualAnimeStyle(
    val title: String,
    val subtitle: String,
    val promptTag: String,
    val details: String,
    val iconName: String
) {
    STYLE_2D_ANIME(
        title = "2D Anime",
        subtitle = "Classic Hand-Drawn Cel Shading",
        promptTag = "classic 2d anime aesthetic, crisp lineart, vibrant cel shading, studio anime production quality, 8k anime art",
        details = "Classic Japanese 2D animation style with expressive outlines, hand-drawn aesthetic, and clean cel shading.",
        iconName = "palette"
    ),
    STYLE_3D_ANIME(
        title = "3D Anime",
        subtitle = "Stylized 3D Anime CGI & Cartoon",
        promptTag = "stylized 3d anime render, smooth 3d character surfaces, vivid lighting, modern anime cgi, unreal engine 5 anime shaders",
        details = "Vibrant 3D animated anime aesthetic blending expressiveness with dynamic camera geometry.",
        iconName = "view_in_ar"
    ),
    STYLE_PREMIUM_ANIME(
        title = "Premium Anime",
        subtitle = "High-Budget Ufotable / Mappa Aesthetic",
        promptTag = "premium high-budget anime production, dynamic particle effects, fluid action sakuga, glowing energy auras, masterclass composition",
        details = "Ultra-polished animation aesthetic featuring high sakuga choreography, radiant particle auras, and dramatic depth.",
        iconName = "auto_awesome"
    ),
    STYLE_CINEMATIC_ANIME(
        title = "Cinematic Anime",
        subtitle = "Atmospheric Makoto Shinkai Film Quality",
        promptTag = "masterpiece cinematic anime film, makoto shinkai style, volumetric lighting, god rays, atmospheric haze, depth of field, 8k wallpaper",
        details = "Breathtaking visual depth with photorealistic anime skies, glowing particle effects, and atmospheric lighting.",
        iconName = "movie_filter"
    )
}

enum class AnimeLanguage(
    val id: String,
    val displayName: String,
    val localeCode: String
) {
    HINDI("hindi", "Hindi", "hi-IN"),
    ENGLISH("english", "English", "en-US")
}

enum class NarratorVoiceGender(
    val id: String,
    val displayName: String,
    val description: String
) {
    MALE("male", "Male", "Deep, resonant anime narrator voice"),
    FEMALE("female", "Female", "Expressive, clear anime narrator voice")
}

enum class VideoAspectRatio(
    val label: String,
    val ratioValue: String,
    val width: Int,
    val height: Int,
    val description: String
) {
    PORTRAIT_9_16("9:16", "9:16", 1080, 1920, "Vertical Shorts & Reels (Recommended)"),
    LANDSCAPE_16_9("16:9", "16:9", 1920, 1080, "Cinematic Widescreen"),
    SQUARE_1_1("1:1", "1:1", 1080, 1080, "Square Feed")
}

data class CameraMovementOption(
    val id: String,
    val name: String,
    val description: String,
    val promptModifier: String
)

val AvailableCameraMovements = listOf(
    CameraMovementOption("dynamic_pan", "Dynamic Cinematic Pan", "Sweeping camera across scenic landscape", "smooth cinematic camera pan, establishing scale"),
    CameraMovementOption("orbit_track", "Orbit Subject Tracking", "360-degree circling around the main character", "360 orbit camera movement focusing on character action"),
    CameraMovementOption("dolly_zoom", "Dolly Zoom (Vertigo)", "Dramatic perspective warp for intense moments", "dolly zoom vertigo effect, dramatic tension"),
    CameraMovementOption("shonen_push", "Shonen Speed Push-in", "Rapid forward zoom with anime speed lines", "rapid anime camera push in with kinetic motion blur"),
    CameraMovementOption("dutch_tilt", "Dutch Angle Dynamic", "Stylized tilted angle for combat and suspense", "stylized dutch angle composition, dynamic framing")
)

data class AnimeLightingEffect(
    val id: String,
    val name: String,
    val description: String,
    val promptModifier: String
)

val AvailableAnimeEffects = listOf(
    AnimeLightingEffect("sakura_bloom", "Sakura Petal Bloom", "Floating cherry blossoms with soft glow", "floating sakura cherry blossom petals, ethereal glow"),
    AnimeLightingEffect("energy_aura", "Super Saiyan / Ki Aura", "Crackling energy flares & power aura", "crackling neon aura energy particles, electric sparks"),
    AnimeLightingEffect("god_rays", "Volumetric Sun Rays", "Atmospheric beam illumination", "volumetric god rays, atmospheric haze, dust motes"),
    AnimeLightingEffect("cyber_neon", "Neo-Tokyo Cyber Glow", "Vivid neon city reflections & rain", "neon cyberpunk reflections, wet asphalt, vivid cyan-magenta glow"),
    AnimeLightingEffect("speed_lines", "Action Speed Lines", "Kinetic impact lines for high velocity", "anime kinetic action lines, high speed streaks")
)

/**
 * Real-time generation stages as requested:
 * Preparing -> Sending Request -> Generating Video -> Generating Narration -> Processing Audio -> Finalizing Video -> Complete
 */
enum class GenerationStage(
    val stageNumber: Int,
    val title: String,
    val description: String
) {
    IDLE(0, "Ready", "Configure parameters and click Generate Video"),
    PREPARING(1, "Preparing", "Validating prompts and structuring scene continuity"),
    SENDING_REQUEST(2, "Sending Request", "Connecting to secure video generation backend"),
    GENERATING_VIDEO(3, "Generating Video", "AI provider rendering anime video frames"),
    GENERATING_NARRATION(4, "Generating Narration", "Synthesizing voice narration in selected language"),
    PROCESSING_AUDIO(5, "Processing Audio", "Mixing speech, background music, and anime SFX"),
    FINALIZING_VIDEO(6, "Finalizing Video", "Encoding 9:16 vertical video master"),
    COMPLETE(7, "Complete", "Video generation complete and ready for preview & download"),
    FAILED(0, "Pipeline Stopped", "Generation encountered an issue or requires API key configuration")
}

data class HindiVoicePreset(
    val id: String,
    val name: String,
    val gender: String,
    val tone: String,
    val sampleTextHindi: String,
    val sampleTextEnglish: String
)

val HindiNarratorPresets = listOf(
    HindiVoicePreset(
        id = "narrator_male_dramatic",
        name = "Kabir (Dramatic Male Narrator)",
        gender = "Male",
        tone = "Deep, Epic & Cinematic",
        sampleTextHindi = "अंधेरे की गहराइयों में, एक नई ताक़त का जन्म हो रहा था...",
        sampleTextEnglish = "In the depths of darkness, a new power was awakening..."
    ),
    HindiVoicePreset(
        id = "narrator_female_heroic",
        name = "Meera (Expressive Female Narrator)",
        gender = "Female",
        tone = "Emotional, Heroic & Expressive",
        sampleTextHindi = "जब उम्मीद की किरण खो चुकी थी, तब उस योद्धा ने कदम बढ़ाया...",
        sampleTextEnglish = "When all hope seemed lost, the warrior stepped forward into the storm..."
    ),
    HindiVoicePreset(
        id = "narrator_male_hype",
        name = "Arjun (Shonen Hype Narrator)",
        gender = "Male",
        tone = "Energetic, Fast-Paced & Thrilling",
        sampleTextHindi = "अब शुरू होता है इस सदी का सबसे बड़ा महायुद्ध!",
        sampleTextEnglish = "Now begins the greatest battle of this century!"
    ),
    HindiVoicePreset(
        id = "narrator_female_soft",
        name = "Pooja (Gentle Storyteller)",
        gender = "Female",
        tone = "Calm, Mystical & Emotional",
        sampleTextHindi = "नियति की इस यात्रा में, हर कदम पर एक नया रहस्य छुपा था...",
        sampleTextEnglish = "On this journey of destiny, every step held a new mystery..."
    )
)

val HindiCharacterPresets = listOf(
    HindiVoicePreset(
        id = "char_rohan_hero",
        name = "Rohan (Shonen Protagonist)",
        gender = "Male",
        tone = "Passionate, Brave & Determined",
        sampleTextHindi = "मैं कभी हार नहीं मानूँगा, चाहे जो हो जाए!",
        sampleTextEnglish = "I will never give up, no matter what happens!"
    ),
    HindiVoicePreset(
        id = "char_priya_heroine",
        name = "Priya (Fierce Heroine)",
        gender = "Female",
        tone = "Confident, Sharp & Compassionate",
        sampleTextHindi = "अपनी तलवार उठाओ! हम एक साथ लड़ेंगे!",
        sampleTextEnglish = "Raise your blade! We will fight together!"
    ),
    HindiVoicePreset(
        id = "char_vikram_rival",
        name = "Vikram (Dark Antihero / Rival)",
        gender = "Male",
        tone = "Cold, Calculating & Powerful",
        sampleTextHindi = "तुम अभी भी मेरी असली ताक़त से अनजान हो।",
        sampleTextEnglish = "You are still unaware of my true power."
    ),
    HindiVoicePreset(
        id = "char_ananya_mage",
        name = "Ananya (Mystic Mage)",
        gender = "Female",
        tone = "Soft-Spoken, Ethereal & Mysterious",
        sampleTextHindi = "हवा के तत्व... मेरी पुकार सुनो और रक्षा करो!",
        sampleTextEnglish = "Elements of the wind... hear my call and protect us!"
    )
)

data class HindiCharacterVoiceConfig(
    val id: String,
    val characterName: String,
    val role: String,
    val voicePresetId: String,
    val pitch: Float = 1.0f,
    val speed: Float = 1.0f,
    val volume: Float = 0.9f,
    val appearancePrompt: String = "matching character design, consistent hairstyle and costume colors"
)

data class BackgroundMusicTrack(
    val id: String,
    val name: String,
    val genre: String,
    val tempo: String,
    val mood: String
)

val AvailableMusicTracks = listOf(
    BackgroundMusicTrack("bgm_epic_battle", "Thunder & Katana (Epic Battle)", "Orchestral Rock", "145 BPM", "High Adrenaline, Heroic"),
    BackgroundMusicTrack("bgm_emotional_piano", "Sakura Tears (Emotional Strings)", "Orchestral Ballad", "78 BPM", "Heartfelt, Melancholic"),
    BackgroundMusicTrack("bgm_cyber_synth", "Neo-Shinjuku Drive (Synthwave)", "Synthwave / Darksynth", "128 BPM", "Futuristic, Fast Pace"),
    BackgroundMusicTrack("bgm_traditional_taiko", "Ronin Spirit (Taiko & Shamisen)", "Japanese Folk Fusion", "110 BPM", "Mystical, Dramatic"),
    BackgroundMusicTrack("bgm_shonen_nostalgia", "Breeze of Youth (Anime Lofi)", "Chill Anime Lofi", "85 BPM", "Calm, Peaceful")
)

data class AnimeSoundEffectItem(
    val id: String,
    val name: String,
    val category: String,
    val enabled: Boolean = true
)

val DefaultSoundEffects = listOf(
    AnimeSoundEffectItem("sfx_katana_slash", "Katana Energy Slash", "Combat", true),
    AnimeSoundEffectItem("sfx_energy_blast", "Kamehameha / Energy Blast", "Combat", true),
    AnimeSoundEffectItem("sfx_whoosh", "Kinetic Speed Dash & Whoosh", "Motion", true),
    AnimeSoundEffectItem("sfx_magic_sparkle", "Ethereal Magic Chime", "Fantasy", true),
    AnimeSoundEffectItem("sfx_thunder", "Cinematic Thunder Roar", "Atmosphere", true),
    AnimeSoundEffectItem("sfx_rain", "Tokyo Rain & Urban Ambience", "Atmosphere", true)
)

data class StorySceneSpec(
    val sceneNumber: Int,
    val title: String,
    val durationSeconds: Int,
    val visualPrompt: String,
    val cameraMovement: String,
    val narratorHindiDialogue: String,
    val characterDialogue: String,
    val transition: String = "Crossfade",
    val sfxTrigger: String = "Katana Slash"
)
