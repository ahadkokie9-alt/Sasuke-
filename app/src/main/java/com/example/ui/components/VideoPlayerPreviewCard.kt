package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AnimeProjectEntity
import com.example.model.VisualAnimeStyle
import com.example.ui.theme.SleekCard
import com.example.ui.theme.SleekCyanGlow
import com.example.ui.theme.SleekDarkBg
import com.example.ui.theme.SleekEmeraldGlow
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekPurplePrimary
import com.example.ui.theme.SleekSlate200
import com.example.ui.theme.SleekSlate400
import com.example.ui.theme.SleekSlate500
import com.example.ui.theme.SleekSlate700
import com.example.ui.theme.SleekSlate800
import com.example.ui.theme.SleekSurface

@Composable
fun VideoPlayerPreviewCard(
    project: AnimeProjectEntity?,
    videoUrl: String?,
    isPlaying: Boolean,
    playheadSeconds: Float,
    durationSeconds: Int,
    visualStyle: VisualAnimeStyle,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VIDEO PREVIEW (9:16 VERTICAL)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = SleekSlate400
            )

            if (videoUrl != null || project?.videoPreviewUrl != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SleekEmeraldGlow.copy(alpha = 0.15f))
                        .border(1.dp, SleekEmeraldGlow.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "1080x1920 Master",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = SleekEmeraldGlow
                    )
                }
            }
        }

        // 9:16 Vertical Video Screen Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SleekDarkBg,
                            Color(0xFF1E1035),
                            Color(0xFF0F081D)
                        )
                    )
                )
                .border(1.dp, SleekSlate800, RoundedCornerShape(24.dp))
                .clickable { onTogglePlay() }
                .testTag("video_preview_canvas"),
            contentAlignment = Alignment.Center
        ) {
            // Background Visual Canvas Art
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tags Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = visualStyle.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = SleekPurpleLight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${durationSeconds}s Anime Master",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = SleekCyanGlow
                        )
                    }
                }

                // Middle: Play / Pause Center Trigger
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPlaying) Color.Black.copy(alpha = 0.5f)
                            else SleekPurplePrimary.copy(alpha = 0.85f)
                        )
                        .border(2.dp, SleekPurpleLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Bottom: Subtitles & HUD Overlay
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = SleekCyanGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = project?.storyPrompt?.take(70) ?: "Anime Master ready for rendering...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                ),
                                color = SleekSlate200,
                                maxLines = 2
                            )
                        }
                    }

                    // Scrubber Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = String.format("%02d:%02d", (playheadSeconds / 60).toInt(), (playheadSeconds % 60).toInt()),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = SleekSlate400
                        )

                        Slider(
                            value = playheadSeconds,
                            onValueChange = onSeek,
                            valueRange = 0f..durationSeconds.toFloat().coerceAtLeast(10f),
                            colors = SliderDefaults.colors(
                                thumbColor = SleekPurpleLight,
                                activeTrackColor = SleekPurplePrimary,
                                inactiveTrackColor = SleekSlate800
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("video_scrubber_slider")
                        )

                        Text(
                            text = String.format("%02d:%02d", (durationSeconds / 60), (durationSeconds % 60)),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = SleekSlate400
                        )
                    }
                }
            }
        }

        // Action Buttons Row (Download Video Button & Share)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onDownload,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekPurplePrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("download_video_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download Video",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
    }
}
