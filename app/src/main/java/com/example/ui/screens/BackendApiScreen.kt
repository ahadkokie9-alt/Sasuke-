package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCard
import com.example.ui.theme.SleekCyanAccent
import com.example.ui.theme.SleekErrorRed
import com.example.ui.theme.SleekPurpleGlow
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekPurplePrimary
import com.example.ui.theme.SleekSlate100
import com.example.ui.theme.SleekSlate200
import com.example.ui.theme.SleekSlate400
import com.example.ui.theme.SleekSlate500
import com.example.ui.theme.SleekSlate800
import com.example.ui.theme.SleekSuccessGreen
import com.example.ui.theme.SleekSurface
import com.example.viewmodel.StudioFormState

@Composable
fun BackendApiScreen(
    formState: StudioFormState,
    onUpdateReplicateTokenInput: (String) -> Unit,
    onSaveReplicateToken: () -> Unit,
    onUpdateHindiVoiceKeyInput: (String) -> Unit,
    onSaveHindiVoiceKey: () -> Unit,
    onClearKeys: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "AI Engines & Backend Services",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = SleekSlate100
                )
                Text(
                    text = "Real AI providers automatically configured for video generation and narration",
                    style = MaterialTheme.typography.bodySmall,
                    color = SleekSlate400
                )
            }
        }

        item {
            // 1. Auto-Selected Video Provider Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekCard)
                    .border(1.dp, SleekSlate800, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .testTag("auto_video_provider_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SleekPurplePrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = SleekPurpleLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Replicate AI Video Engine",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SleekSlate100
                                )
                                Text(
                                    text = "Auto-Configured Model: MiniMax Video-01 (Hailuo AI)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = SleekPurpleLight
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (formState.isSecretTokenConfigured) SleekSuccessGreen.copy(alpha = 0.15f)
                                    else SleekPurplePrimary.copy(alpha = 0.15f)
                                )
                                .border(
                                    1.dp,
                                    if (formState.isSecretTokenConfigured) SleekSuccessGreen.copy(alpha = 0.4f)
                                    else SleekPurpleGlow.copy(alpha = 0.3f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (formState.isSecretTokenConfigured) "Active & Ready" else "Key Required",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (formState.isSecretTokenConfigured) SleekSuccessGreen else SleekPurpleGlow
                            )
                        }
                    }

                    Text(
                        text = "Real cloud GPU video diffusion. The backend automatically dispatches jobs to Wan 2.1 (wan-video/wan-2.1-t2v-480p). To enable GPU rendering, provide REPLICATE_API_TOKEN.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = SleekSlate400
                    )

                    // Token input field (optional developer override if not in Secrets panel)
                    OutlinedTextField(
                        value = formState.customReplicateTokenInput,
                        onValueChange = onUpdateReplicateTokenInput,
                        placeholder = {
                            Text(
                                text = if (formState.isSecretTokenConfigured) "•••••••••••••••• (Active via Secrets / Env)" else "Enter REPLICATE_API_TOKEN (r8_...)",
                                color = SleekSlate500,
                                fontSize = 12.sp
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SleekPurplePrimary,
                            unfocusedBorderColor = SleekSlate800,
                            focusedTextColor = SleekSlate100,
                            unfocusedTextColor = SleekSlate200,
                            focusedContainerColor = SleekSurface,
                            unfocusedContainerColor = SleekSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("replicate_token_text_field"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onSaveReplicateToken() })
                    )

                    if (formState.customReplicateTokenInput.isNotBlank()) {
                        Button(
                            onClick = onSaveReplicateToken,
                            colors = ButtonDefaults.buttonColors(containerColor = SleekPurplePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_replicate_token_btn")
                        ) {
                            Text(
                                text = "Save Token Override",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        item {
            // 2. Auto-Selected TTS Voice Provider Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekCard)
                    .border(1.dp, SleekSlate800, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .testTag("auto_tts_provider_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SleekCyanAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = SleekCyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Android Neural Speech Engine",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SleekSlate100
                                )
                                Text(
                                    text = "Hindi (hi-IN) & English (en-US) Native Synthesis",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = SleekCyanAccent
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SleekSuccessGreen.copy(alpha = 0.15f))
                                .border(1.dp, SleekSuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Active (Built-In)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SleekSuccessGreen
                            )
                        }
                    }

                    Text(
                        text = "Real on-device speech synthesis engine with custom pitch and speed modulation for narrator and character dialogues in both Hindi and English. No external API key required.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = SleekSlate400
                    )
                }
            }
        }

        item {
            // Security Best Practices Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekCard)
                    .border(1.dp, SleekSlate800, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = SleekPurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Server-Side Secret Isolation",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = SleekSlate100
                        )
                        Text(
                            text = "All API keys are resolved securely through BuildConfig and the Secrets Gradle Plugin. No credentials are hardcoded or exposed to the public UI.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = SleekSlate400
                        )
                    }
                }
            }
        }

        item {
            if (formState.isSecretTokenConfigured) {
                OutlinedButton(
                    onClick = onClearKeys,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekSlate800)
                ) {
                    Text(
                        text = "Reset Stored Secrets",
                        style = MaterialTheme.typography.labelMedium,
                        color = SleekErrorRed
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
