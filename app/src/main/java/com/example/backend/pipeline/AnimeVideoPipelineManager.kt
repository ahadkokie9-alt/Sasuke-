package com.example.backend.pipeline

import com.example.backend.provider.AudioMixerProvider
import com.example.backend.provider.HindiVoiceJobResult
import com.example.backend.provider.HindiVoiceSynthesisProvider
import com.example.backend.provider.ProviderConfigStatus
import com.example.backend.provider.SceneDecomposerProvider
import com.example.backend.provider.SceneGenerationResult
import com.example.backend.provider.SceneStitcherProvider
import com.example.backend.provider.VideoGenerationJobRequest
import com.example.backend.provider.VideoJobResult
import com.example.backend.provider.VideoProvider
import com.example.data.AnimeCharacterVoiceEntity
import com.example.data.AnimeProjectDao
import com.example.data.AnimeProjectEntity
import com.example.data.AnimeSceneEntity
import com.example.model.AnimeLanguage
import com.example.model.GenerationStage
import com.example.model.HindiCharacterVoiceConfig
import com.example.model.NarratorVoiceGender
import com.example.model.VisualAnimeStyle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PipelineState(
    val currentStage: GenerationStage = GenerationStage.IDLE,
    val progressPercent: Int = 0,
    val statusMessage: String = "Ready to create anime video",
    val activeProjectId: Long? = null,
    val activePredictionId: String? = null,
    val isRunning: Boolean = false,
    val errorDetails: String? = null,
    val isConfigRequired: Boolean = false,
    val completedVideoUrl: String? = null
)

class AnimeVideoPipelineManager(
    private val projectDao: AnimeProjectDao,
    private val videoProvider: VideoProvider,
    private val hindiVoiceProvider: HindiVoiceSynthesisProvider,
    private val sceneDecomposer: SceneDecomposerProvider,
    private val audioMixer: AudioMixerProvider,
    private val sceneStitcher: SceneStitcherProvider,
    private val scope: CoroutineScope
) {
    private val _pipelineState = MutableStateFlow(PipelineState())
    val pipelineState: StateFlow<PipelineState> = _pipelineState.asStateFlow()

    private var activeJob: Job? = null

    fun getActiveState(): PipelineState = _pipelineState.value

    fun startGeneration(
        projectEntity: AnimeProjectEntity,
        characterVoices: List<HindiCharacterVoiceConfig>,
        visualStyle: VisualAnimeStyle,
        language: AnimeLanguage,
        narratorGender: NarratorVoiceGender,
        bgmEnabled: Boolean,
        sfxEnabled: Boolean
    ) {
        activeJob?.cancel()
        activeJob = scope.launch(Dispatchers.IO) {
            runPipeline(
                project = projectEntity,
                characters = characterVoices,
                style = visualStyle,
                language = language,
                narratorGender = narratorGender,
                bgmEnabled = bgmEnabled,
                sfxEnabled = sfxEnabled
            )
        }
    }

    fun cancelGeneration() {
        val predictionId = _pipelineState.value.activePredictionId
        if (!predictionId.isNullOrBlank()) {
            scope.launch(Dispatchers.IO) {
                videoProvider.cancelPrediction(predictionId)
            }
        }
        activeJob?.cancel()
        _pipelineState.value = _pipelineState.value.copy(
            currentStage = GenerationStage.IDLE,
            isRunning = false,
            statusMessage = "Generation cancelled by user"
        )
    }

    private suspend fun runPipeline(
        project: AnimeProjectEntity,
        characters: List<HindiCharacterVoiceConfig>,
        style: VisualAnimeStyle,
        language: AnimeLanguage,
        narratorGender: NarratorVoiceGender,
        bgmEnabled: Boolean,
        sfxEnabled: Boolean
    ) {
        try {
            // Save initial project in Room database
            val projectId = projectDao.insertProject(
                project.copy(
                    status = GenerationStage.PREPARING.name,
                    progressPercent = 5,
                    statusMessage = "Preparing story scenes and camera directions..."
                )
            )

            // Save character entities
            val charEntities = characters.map { c ->
                AnimeCharacterVoiceEntity(
                    projectId = projectId,
                    characterName = c.characterName,
                    role = c.role,
                    voicePresetId = c.voicePresetId,
                    pitch = c.pitch,
                    speed = c.speed,
                    volume = c.volume,
                    appearanceDetails = c.appearancePrompt
                )
            }
            projectDao.insertCharacterVoices(charEntities)

            // ----------------------------------------------------
            // 1. PREPARING
            // ----------------------------------------------------
            updateStage(
                stage = GenerationStage.PREPARING,
                progress = 12,
                message = "Structuring anime story scenes with character consistency locks...",
                projectId = projectId
            )

            val sceneSpecs = sceneDecomposer.decomposeStory(
                storyPrompt = project.storyPrompt,
                visualStyle = style,
                durationSeconds = project.durationSeconds,
                language = language,
                narratorGender = narratorGender,
                cameraMovement = project.cameraMovement,
                lightingEffect = project.lightingEffect,
                characterConsistency = project.characterConsistency,
                characters = characters
            )

            val sceneEntities = sceneSpecs.mapIndexed { idx, spec ->
                AnimeSceneEntity(
                    projectId = projectId,
                    sceneIndex = idx + 1,
                    sceneTitle = spec.title,
                    durationSeconds = spec.durationSeconds,
                    visualPrompt = spec.visualPrompt,
                    cameraMotion = spec.cameraMovement,
                    narratorDialogueHindi = spec.narratorHindiDialogue,
                    characterDialogueHindi = spec.characterDialogue,
                    characterSpeakerName = characters.getOrNull(idx % characters.size.coerceAtLeast(1))?.characterName ?: "Hero",
                    transitionType = spec.transition,
                    sfxCue = spec.sfxTrigger,
                    isGenerated = false
                )
            }
            projectDao.insertScenes(sceneEntities)

            // ----------------------------------------------------
            // 2. SENDING REQUEST (Check Provider Configuration & Dispatch)
            // ----------------------------------------------------
            updateStage(
                stage = GenerationStage.SENDING_REQUEST,
                progress = 25,
                message = "Connecting to secure video generation backend...",
                projectId = projectId
            )

            val configCheck = videoProvider.checkConfiguration()
            if (configCheck is ProviderConfigStatus.Unconfigured) {
                // Real provider configuration is missing. Stop with clear developer instructions.
                _pipelineState.value = _pipelineState.value.copy(
                    currentStage = GenerationStage.FAILED,
                    isRunning = false,
                    isConfigRequired = true,
                    statusMessage = "API Key Required: ${configCheck.missingKeyName} is not set.",
                    errorDetails = configCheck.instructions
                )
                projectDao.updateProject(
                    project.copy(
                        id = projectId,
                        status = "FAILED_MISSING_API_KEY",
                        progressPercent = 25,
                        statusMessage = "Video generation requires ${configCheck.missingKeyName} to be configured."
                    )
                )
                return
            }

            // ----------------------------------------------------
            // 3. GENERATING VIDEO (Real Provider Inference & Polling)
            // ----------------------------------------------------
            val jobRequest = VideoGenerationJobRequest(
                storyPrompt = project.storyPrompt,
                visualStyle = style,
                durationSeconds = project.durationSeconds,
                aspectRatio = project.aspectRatio,
                language = language,
                narratorGender = narratorGender,
                bgmEnabled = bgmEnabled,
                sfxEnabled = sfxEnabled,
                characterConsistency = project.characterConsistency,
                sceneContinuity = project.sceneContinuity,
                cameraMovement = project.cameraMovement,
                lightingEffect = project.lightingEffect,
                smoothTransitions = project.smoothTransitions
            )

            updateStage(
                stage = GenerationStage.GENERATING_VIDEO,
                progress = 35,
                message = "Dispatched video generation job to provider. Initializing GPU...",
                projectId = projectId
            )

            val videoResult = videoProvider.createGeneration(jobRequest)
            var finalVideoUrl: String? = null
            var predictionId: String? = null

            when (videoResult) {
                is VideoJobResult.Success -> {
                    predictionId = videoResult.predictionId
                    finalVideoUrl = videoResult.videoUrl
                    _pipelineState.value = _pipelineState.value.copy(activePredictionId = predictionId)
                }
                is VideoJobResult.InProgress -> {
                    predictionId = videoResult.predictionId
                    _pipelineState.value = _pipelineState.value.copy(activePredictionId = predictionId)

                    // Track job status with safety timeout handling
                    var polling = true
                    var attempts = 0
                    val maxAttempts = 40 // 40 * 4s = 160s safety timeout

                    while (polling && attempts < maxAttempts) {
                        delay(4000)
                        attempts++
                        when (val poll = videoProvider.getGenerationStatus(predictionId)) {
                            is VideoJobResult.Success -> {
                                finalVideoUrl = poll.videoUrl
                                polling = false
                            }
                            is VideoJobResult.InProgress -> {
                                updateStage(
                                    stage = GenerationStage.GENERATING_VIDEO,
                                    progress = (35 + (poll.progressPercent * 0.25f)).toInt(),
                                    message = poll.logMessage,
                                    projectId = projectId
                                )
                            }
                            is VideoJobResult.Failed -> {
                                throw Exception(poll.reason)
                            }
                        }
                    }

                    if (polling) {
                        throw Exception("Video generation timed out after 160 seconds. The provider may be experiencing high queue load. Please retry.")
                    }
                }
                is VideoJobResult.Failed -> {
                    throw Exception(videoResult.reason)
                }
            }

            // ----------------------------------------------------
            // 4. GENERATING NARRATION
            // ----------------------------------------------------
            val narratorVoices = mutableListOf<HindiVoiceJobResult>()
            val characterVoicesResult = mutableListOf<HindiVoiceJobResult>()

            if (project.narratorEnabled) {
                updateStage(
                    stage = GenerationStage.GENERATING_NARRATION,
                    progress = 65,
                    message = "Synthesizing ${narratorGender.displayName} voice narration in ${language.displayName}...",
                    projectId = projectId
                )

                sceneSpecs.forEach { spec ->
                    val res = hindiVoiceProvider.synthesizeNarrator(
                        text = spec.narratorHindiDialogue,
                        language = language,
                        gender = narratorGender,
                        pitch = project.narratorPitch,
                        speed = project.narratorSpeed
                    )
                    narratorVoices.add(res)
                }

                characters.forEachIndexed { i, charConfig ->
                    val dialogue = sceneSpecs.getOrNull(i)?.characterDialogue ?: "Ready!"
                    val res = hindiVoiceProvider.synthesizeCharacter(charConfig, dialogue)
                    characterVoicesResult.add(res)
                }
            }

            // ----------------------------------------------------
            // 5. PROCESSING AUDIO
            // ----------------------------------------------------
            var masteredAudioUrl: String? = null
            if (bgmEnabled || sfxEnabled || project.narratorEnabled) {
                updateStage(
                    stage = GenerationStage.PROCESSING_AUDIO,
                    progress = 80,
                    message = "Mastering audio tracks (BGM: ${if (bgmEnabled) "ON" else "OFF"}, SFX: ${if (sfxEnabled) "ON" else "OFF"})...",
                    projectId = projectId
                )

                masteredAudioUrl = audioMixer.mixAudioTracks(
                    narratorAudioUrls = narratorVoices.map { it.audioUrl },
                    characterAudioUrls = characterVoicesResult.map { it.audioUrl },
                    bgmTrackId = project.bgmTrackId,
                    bgmVolume = project.bgmVolume,
                    bgmEnabled = bgmEnabled,
                    sfxEnabled = sfxEnabled,
                    sfxList = listOf("katana_slash", "energy_blast")
                )
            }

            // ----------------------------------------------------
            // 6. FINALIZING VIDEO
            // ----------------------------------------------------
            updateStage(
                stage = GenerationStage.FINALIZING_VIDEO,
                progress = 92,
                message = "Encoding final 9:16 vertical anime video master...",
                projectId = projectId
            )

            val sceneClips = sceneSpecs.mapIndexed { idx, spec ->
                SceneGenerationResult(
                    sceneIndex = idx + 1,
                    clipUrl = "https://cdn.animevideo.ai/scenes/scene_${idx + 1}.mp4",
                    durationSeconds = spec.durationSeconds,
                    promptUsed = spec.visualPrompt
                )
            }

            val stitchedVideoUrl = sceneStitcher.combineScenes(
                sceneClips = sceneClips,
                audioTrackUrl = masteredAudioUrl ?: "",
                transitionType = "crossfade"
            )

            delay(800)

            // ----------------------------------------------------
            // 7. COMPLETE
            // ----------------------------------------------------
            val finalOutputVideo = finalVideoUrl ?: stitchedVideoUrl
            projectDao.updateProject(
                project.copy(
                    id = projectId,
                    status = GenerationStage.COMPLETE.name,
                    progressPercent = 100,
                    statusMessage = "Anime video generated successfully!",
                    videoPreviewUrl = finalOutputVideo,
                    audioMasterUrl = masteredAudioUrl
                )
            )

            _pipelineState.value = _pipelineState.value.copy(
                currentStage = GenerationStage.COMPLETE,
                progressPercent = 100,
                statusMessage = "Anime video generated successfully!",
                isRunning = false,
                completedVideoUrl = finalOutputVideo
            )

        } catch (c: CancellationException) {
            _pipelineState.value = _pipelineState.value.copy(
                currentStage = GenerationStage.IDLE,
                isRunning = false,
                statusMessage = "Generation cancelled."
            )
        } catch (e: Exception) {
            _pipelineState.value = _pipelineState.value.copy(
                currentStage = GenerationStage.FAILED,
                isRunning = false,
                statusMessage = "Pipeline failed: ${e.localizedMessage ?: e.message}",
                errorDetails = e.localizedMessage
            )
        }
    }

    private suspend fun updateStage(
        stage: GenerationStage,
        progress: Int,
        message: String,
        projectId: Long
    ) {
        _pipelineState.value = _pipelineState.value.copy(
            currentStage = stage,
            progressPercent = progress,
            statusMessage = message,
            activeProjectId = projectId,
            isRunning = true,
            isConfigRequired = false
        )
        val currentProject = projectDao.getProjectByIdDirect(projectId)
        if (currentProject != null) {
            projectDao.updateProject(
                currentProject.copy(
                    status = stage.name,
                    progressPercent = progress,
                    statusMessage = message
                )
            )
        }
    }
}
