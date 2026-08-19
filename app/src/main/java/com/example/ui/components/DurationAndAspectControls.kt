package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnimeLanguage
import com.example.model.NarratorVoiceGender
import com.example.ui.theme.SleekCard
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekPurplePrimary
import com.example.ui.theme.SleekSlate200
import com.example.ui.theme.SleekSlate300
import com.example.ui.theme.SleekSlate400
import com.example.ui.theme.SleekSlate500
import com.example.ui.theme.SleekSlate600
import com.example.ui.theme.SleekSlate800
import com.example.ui.theme.SleekSurface

@Composable
fun DurationAndAspectControls(
    selectedDuration: Int,
    selectedLanguage: AnimeLanguage,
    selectedNarratorGender: NarratorVoiceGender,
    bgmEnabled: Boolean,
    sfxEnabled: Boolean,
    onSelectDuration: (Int) -> Unit,
    onSelectLanguage: (AnimeLanguage) -> Unit,
    onSelectNarratorGender: (NarratorVoiceGender) -> Unit,
    onToggleBgm: (Boolean) -> Unit,
    onToggleSfx: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDurationMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Duration & Language Selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Duration Selector Card (10s, 15s, 30s, 60s)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekCard)
                    .border(1.dp, SleekSlate800, RoundedCornerShape(16.dp))
                    .clickable { showDurationMenu = true }
                    .padding(12.dp)
                    .testTag("duration_selector_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = SleekPurpleLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "DURATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = SleekSlate500
                            )
                            Text(
                                text = "$selectedDuration seconds",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = SleekSlate200
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = SleekSlate600,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showDurationMenu,
                    onDismissRequest = { showDurationMenu = false },
                    modifier = Modifier.background(SleekSurface)
                ) {
                    listOf(6, 10, 15, 30, 60).forEach { seconds ->
                        val label = if (seconds == 6) "6 seconds (MiniMax HD)" else "$seconds seconds"
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = label,
                                    color = if (seconds == selectedDuration) SleekPurpleLight else SleekSlate200
                                )
                            },
                            onClick = {
                                onSelectDuration(seconds)
                                showDurationMenu = false
                            }
                        )
                    }
                }
            }

            // Language Selector Card (Hindi / English)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekCard)
                    .border(1.dp, SleekSlate800, RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .testTag("language_selector_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = SleekPurpleLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "LANGUAGE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = SleekSlate500
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(AnimeLanguage.HINDI, AnimeLanguage.ENGLISH).forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SleekPurplePrimary else SleekSurface)
                                    .clickable { onSelectLanguage(lang) }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else SleekSlate400
                                )
                            }
                        }
                    }
                }
            }
        }

        // Row 2: Narrator Voice Gender Selector (Male / Female)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SleekCard)
                .border(1.dp, SleekSlate800, RoundedCornerShape(16.dp))
                .padding(12.dp)
                .testTag("narrator_voice_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = SleekPurpleLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "NARRATOR VOICE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = SleekSlate500
                        )
                        Text(
                            text = selectedNarratorGender.displayName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = SleekSlate200
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(NarratorVoiceGender.MALE, NarratorVoiceGender.FEMALE).forEach { gender ->
                        val isSelected = selectedNarratorGender == gender
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SleekPurplePrimary else SleekSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) SleekPurpleLight.copy(alpha = 0.5f) else SleekSlate800,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectNarratorGender(gender) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("voice_gender_${gender.id}")
                        ) {
                            Text(
                                text = gender.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else SleekSlate400
                            )
                        }
                    }
                }
            }
        }

        // Row 3: Audio Toggles (Background Music ON/OFF, Anime SFX ON/OFF)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Background Music Toggle
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekCard)
                    .border(1.dp, SleekSlate800, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = SleekPurpleLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "BGM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SleekSlate500
                            )
                            Text(
                                text = if (bgmEnabled) "ON" else "OFF",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (bgmEnabled) SleekPurpleLight else SleekSlate400
                            )
                        }
                    }

                    Switch(
                        checked = bgmEnabled,
                        onCheckedChange = onToggleBgm,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SleekPurplePrimary,
                            uncheckedTrackColor = SleekSlate800
                        ),
                        modifier = Modifier.testTag("bgm_toggle_switch")
                    )
                }
            }

            // Anime SFX Toggle
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekCard)
                    .border(1.dp, SleekSlate800, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = SleekPurpleLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "SFX",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SleekSlate500
                            )
                            Text(
                                text = if (sfxEnabled) "ON" else "OFF",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (sfxEnabled) SleekPurpleLight else SleekSlate400
                            )
                        }
                    }

                    Switch(
                        checked = sfxEnabled,
                        onCheckedChange = onToggleSfx,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SleekPurplePrimary,
                            uncheckedTrackColor = SleekSlate800
                        ),
                        modifier = Modifier.testTag("sfx_toggle_switch")
                    )
                }
            }
        }
    }
}
