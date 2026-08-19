package com.example.backend.provider

import com.example.model.AnimeLanguage
import com.example.model.HindiCharacterVoiceConfig
import com.example.model.NarratorVoiceGender
import com.example.model.StorySceneSpec
import com.example.model.VisualAnimeStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SceneDecomposerProviderImpl : SceneDecomposerProvider {

    override suspend fun decomposeStory(
        storyPrompt: String,
        visualStyle: VisualAnimeStyle,
        durationSeconds: Int,
        language: AnimeLanguage,
        narratorGender: NarratorVoiceGender,
        cameraMovement: String,
        lightingEffect: String,
        characterConsistency: Boolean,
        characters: List<HindiCharacterVoiceConfig>
    ): List<StorySceneSpec> = withContext(Dispatchers.Default) {
        val sceneDuration = (durationSeconds / 4).coerceAtLeast(3)
        val char1 = characters.getOrNull(0)?.characterName ?: "Hero"
        val char2 = characters.getOrNull(1)?.characterName ?: "Rival"

        val consistencyNote = if (characterConsistency) {
            "Maintain exact character appearance, hairstyle, and signature costume colors."
        } else ""

        val isHindi = language == AnimeLanguage.HINDI

        listOf(
            StorySceneSpec(
                sceneNumber = 1,
                title = "Scene 1: World Intro & Hero Awakening",
                durationSeconds = sceneDuration,
                visualPrompt = "Establishing wide cinematic anime shot. $storyPrompt. $lightingEffect, $consistencyNote Focusing on $char1 standing with determination.",
                cameraMovement = "Dynamic Pan & Slow Zoom-in",
                narratorHindiDialogue = if (isHindi) "एक प्राचीन भविष्यवाणी के अनुसार, अंधेरे और रोशनी का संतुलन डगमगा रहा था।" else "According to an ancient prophecy, the balance of darkness and light was trembling.",
                characterDialogue = if (isHindi) "$char1: 'मेरा सफर यहाँ से शुरू होता है!'" else "$char1: 'My journey begins here and now!'",
                transition = "Crossfade",
                sfxTrigger = "Tokyo Rain & Urban Ambience"
            ),
            StorySceneSpec(
                sceneNumber = 2,
                title = "Scene 2: Rising Tension & Confrontation",
                durationSeconds = sceneDuration,
                visualPrompt = "Mid-range dynamic cut. $char1 meets $char2. Electric sparks and anime eye flare closeup. $consistencyNote",
                cameraMovement = "Dutch Angle & Orbit Track",
                narratorHindiDialogue = if (isHindi) "रास्ते में खड़ा था एक ऐसा प्रतिद्वंद्वी, जिसकी ताक़त की कोई सीमा नहीं थी।" else "Standing in the path was a formidable rival of boundless power.",
                characterDialogue = if (isHindi) "$char2: 'तुम्हारी हिम्मत की दाद देनी होगी, लेकिन आगे सिर्फ़ हार है!'" else "$char2: 'Your courage is commendable, but only defeat awaits you!'",
                transition = "Whip Pan",
                sfxTrigger = "Kinetic Speed Dash & Whoosh"
            ),
            StorySceneSpec(
                sceneNumber = 3,
                title = "Scene 3: Climax & Special Move Clash",
                durationSeconds = sceneDuration,
                visualPrompt = "High octane action climax. Energy aura explosion, speed lines, shockwaves shattering the arena. $char1 releases ultimate technique. $consistencyNote",
                cameraMovement = "Shonen Speed Push-in & Rapid Tracking",
                narratorHindiDialogue = if (isHindi) "और फिर दोनों शक्तियों का महाविस्फोट हुआ, जिससे पूरा आसमान गूंज उठा!" else "And then came the colossal clash of powers, shaking the heavens!",
                characterDialogue = if (isHindi) "$char1: 'अपनी पूरी ताक़त लगा दो! अग्नि प्रहार!'" else "$char1: 'Unleash your full might! Final Strike!'",
                transition = "Katana Slash Whiteout",
                sfxTrigger = "Katana Energy Slash & Energy Blast"
            ),
            StorySceneSpec(
                sceneNumber = 4,
                title = "Scene 4: Resolution & Cinematic Cliffhanger",
                durationSeconds = sceneDuration,
                visualPrompt = "Cinematic slow motion aftermath. Smoke clears as sakura petals fall over the horizon. $char1 gazes at the distant city skyline. $consistencyNote",
                cameraMovement = "Elevating Aerial Crane Shot",
                narratorHindiDialogue = if (isHindi) "युद्ध समाप्त हो चुका था, पर कहानी का अगला अध्याय अभी शुरू होने वाला था..." else "The battle was over, but the next chapter was just beginning...",
                characterDialogue = if (isHindi) "$char1: 'हम फिर मिलेंगे... नई सुबह के साथ।'" else "$char1: 'We shall meet again... with the new dawn.'",
                transition = "Smooth Fade to Black",
                sfxTrigger = "Ethereal Magic Chime & Thunder"
            )
        )
    }
}

class AudioMixerProviderImpl : AudioMixerProvider {
    override suspend fun mixAudioTracks(
        narratorAudioUrls: List<String>,
        characterAudioUrls: List<String>,
        bgmTrackId: String,
        bgmVolume: Float,
        bgmEnabled: Boolean,
        sfxEnabled: Boolean,
        sfxList: List<String>
    ): String = withContext(Dispatchers.Default) {
        "https://cdn.animevideo.ai/audio/mastered_mix_${System.currentTimeMillis()}.mp3"
    }
}

class SceneStitcherProviderImpl : SceneStitcherProvider {
    override suspend fun combineScenes(
        sceneClips: List<SceneGenerationResult>,
        audioTrackUrl: String,
        transitionType: String
    ): String = withContext(Dispatchers.Default) {
        "https://cdn.animevideo.ai/video/final_anime_video_${System.currentTimeMillis()}.mp4"
    }
}
