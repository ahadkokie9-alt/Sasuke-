package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AnimeProjectEntity
import com.example.ui.components.AnimeHeaderBanner
import com.example.ui.components.DurationAndAspectControls
import com.example.ui.components.GenerationProgressCard
import com.example.ui.components.PromptInputSection
import com.example.ui.components.StyleSelectorGrid
import com.example.ui.components.VideoPlayerPreviewCard
import com.example.ui.theme.SleekDarkBg
import com.example.ui.theme.SleekPurplePrimary
import com.example.ui.theme.SleekSlate800
import com.example.viewmodel.AnimeVideoViewModel

@Composable
fun StudioHomeScreen(
    viewModel: AnimeVideoViewModel,
    onOpenApiSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsState()
    val pipelineState by viewModel.pipelineState.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playhead by viewModel.currentPlayheadSeconds.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()

    val latestProject: AnimeProjectEntity? = allProjects.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SleekDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. AnimeVideo AI Logo & Header Banner
            AnimeHeaderBanner(
                isReplicateConfigured = formState.isSecretTokenConfigured,
                onOpenApiSettings = onOpenApiSettings
            )

            // 2. Large Video Prompt Box
            PromptInputSection(
                promptText = formState.storyPrompt,
                onPromptChange = { viewModel.updateStoryPrompt(it) },
                onSelectTemplate = { viewModel.applyPromptTemplate(it) }
            )

            // 3. Anime Style Selector (2D Anime, 3D Anime, Premium Anime, Cinematic Anime)
            StyleSelectorGrid(
                selectedStyle = formState.selectedStyle,
                onSelectStyle = { viewModel.selectVisualStyle(it) }
            )

            // 4, 5, 6, 7, 8: Duration (10s, 15s, 30s, 60s), Language (Hindi/English), Narrator Gender (Male/Female), BGM & SFX Switches
            DurationAndAspectControls(
                selectedDuration = formState.durationSeconds,
                selectedLanguage = formState.selectedLanguage,
                selectedNarratorGender = formState.selectedNarratorGender,
                bgmEnabled = formState.bgmEnabled,
                sfxEnabled = formState.sfxEnabled,
                onSelectDuration = { viewModel.selectDuration(it) },
                onSelectLanguage = { viewModel.selectLanguage(it) },
                onSelectNarratorGender = { viewModel.selectNarratorGender(it) },
                onToggleBgm = { viewModel.toggleBgm(it) },
                onToggleSfx = { viewModel.toggleSfx(it) }
            )

            // 9. Generate Video Button
            Button(
                onClick = { viewModel.startVideoGeneration() },
                enabled = !pipelineState.isRunning && formState.storyPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekPurplePrimary,
                    disabledContainerColor = SleekSlate800
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("generate_video_primary_btn")
            ) {
                if (pipelineState.isRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Generating Anime Video...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate Video",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }

            // 10. Generation Progress / Status Section (Real-time tracking of 7 stages)
            GenerationProgressCard(
                pipelineState = pipelineState,
                onCancel = { viewModel.cancelVideoGeneration() },
                onOpenApiSettings = onOpenApiSettings
            )

            // 11 & 12: Video Preview Section (9:16 vertical) and Download Video Button
            VideoPlayerPreviewCard(
                project = latestProject,
                videoUrl = pipelineState.completedVideoUrl,
                isPlaying = isPlaying,
                playheadSeconds = playhead,
                durationSeconds = formState.durationSeconds,
                visualStyle = formState.selectedStyle,
                onTogglePlay = { viewModel.togglePlayback() },
                onSeek = { viewModel.seekPlayhead(it) },
                onDownload = {
                    latestProject?.let { viewModel.downloadVideo(it) }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
