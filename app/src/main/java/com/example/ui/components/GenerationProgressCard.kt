package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.backend.pipeline.PipelineState
import com.example.model.GenerationStage
import com.example.ui.theme.SleekCard
import com.example.ui.theme.SleekEmeraldGlow
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekPurplePrimary
import com.example.ui.theme.SleekRedLight
import com.example.ui.theme.SleekSlate200
import com.example.ui.theme.SleekSlate400
import com.example.ui.theme.SleekSlate500
import com.example.ui.theme.SleekSlate800

@Composable
fun GenerationProgressCard(
    pipelineState: PipelineState,
    onCancel: () -> Unit,
    onOpenApiSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = pipelineState.isRunning
    val isComplete = pipelineState.currentStage == GenerationStage.COMPLETE
    val isFailed = pipelineState.currentStage == GenerationStage.FAILED

    if (isRunning || isComplete || isFailed) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SleekCard)
                .border(
                    1.dp,
                    when {
                        isComplete -> SleekEmeraldGlow.copy(alpha = 0.5f)
                        isFailed -> SleekRedLight.copy(alpha = 0.5f)
                        else -> SleekPurplePrimary.copy(alpha = 0.5f)
                    },
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
                .testTag("generation_progress_card")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when {
                            isComplete -> {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SleekEmeraldGlow,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            isFailed -> {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = SleekRedLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            else -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = SleekPurpleLight
                                )
                            }
                        }

                        Text(
                            text = pipelineState.currentStage.title.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = when {
                                isComplete -> SleekEmeraldGlow
                                isFailed -> SleekRedLight
                                else -> SleekPurpleLight
                            }
                        )
                    }

                    Text(
                        text = "${pipelineState.progressPercent}%",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekSlate200
                    )
                }

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (pipelineState.progressPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when {
                        isComplete -> SleekEmeraldGlow
                        isFailed -> SleekRedLight
                        else -> SleekPurplePrimary
                    },
                    trackColor = SleekSlate800
                )

                // Status Message / Log
                Text(
                    text = pipelineState.statusMessage,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    color = SleekSlate300(alpha = 0.9f)
                )

                // Error Details or API config prompt if applicable
                AnimatedVisibility(
                    visible = isFailed && pipelineState.errorDetails != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SleekRedLight.copy(alpha = 0.1f))
                            .border(1.dp, SleekRedLight.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Issue Details:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SleekRedLight
                            )
                            Text(
                                text = pipelineState.errorDetails ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = SleekSlate300(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Stage Stepper Visualizer
                val stages = listOf(
                    GenerationStage.PREPARING,
                    GenerationStage.SENDING_REQUEST,
                    GenerationStage.GENERATING_VIDEO,
                    GenerationStage.GENERATING_NARRATION,
                    GenerationStage.PROCESSING_AUDIO,
                    GenerationStage.FINALIZING_VIDEO,
                    GenerationStage.COMPLETE
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    stages.forEach { stage ->
                        val isPassed = pipelineState.progressPercent >= (stage.stageNumber * 14) || isComplete
                        val isCurrent = pipelineState.currentStage == stage

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isComplete -> SleekEmeraldGlow
                                        isCurrent -> SleekPurpleLight
                                        isPassed -> SleekPurplePrimary
                                        else -> SleekSlate800
                                    }
                                )
                        )
                    }
                }

                // Action Row (Cancel or Configure Secrets)
                if (isRunning) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekSlate400),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("cancel_generation_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
                        }
                    }
                } else if (pipelineState.isConfigRequired) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onOpenApiSettings,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekPurpleLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("open_api_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Configure API Keys", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
                        }
                    }
                }
            }
        }
    }
}

private fun SleekSlate300(alpha: Float): Color = SleekSlate200.copy(alpha = alpha)
