package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VisualAnimeStyle
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekPurplePrimary
import com.example.ui.theme.SleekSlate400
import com.example.ui.theme.SleekSlate800
import com.example.ui.theme.SleekSurface

@Composable
fun StyleSelectorGrid(
    selectedStyle: VisualAnimeStyle,
    onSelectStyle: (VisualAnimeStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ANIME STYLE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = SleekSlate400,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val styles = listOf(
                VisualAnimeStyle.STYLE_2D_ANIME,
                VisualAnimeStyle.STYLE_3D_ANIME,
                VisualAnimeStyle.STYLE_PREMIUM_ANIME,
                VisualAnimeStyle.STYLE_CINEMATIC_ANIME
            )

            styles.forEach { style ->
                val isSelected = selectedStyle == style
                val icon = when (style) {
                    VisualAnimeStyle.STYLE_2D_ANIME -> Icons.Default.Palette
                    VisualAnimeStyle.STYLE_3D_ANIME -> Icons.Default.ViewInAr
                    VisualAnimeStyle.STYLE_PREMIUM_ANIME -> Icons.Default.AutoAwesome
                    VisualAnimeStyle.STYLE_CINEMATIC_ANIME -> Icons.Default.MovieFilter
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) SleekPurplePrimary else SleekSurface)
                        .border(
                            1.dp,
                            if (isSelected) SleekPurpleLight.copy(alpha = 0.6f) else SleekSlate800,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectStyle(style) }
                        .padding(vertical = 12.dp, horizontal = 4.dp)
                        .testTag("style_card_${style.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = style.title,
                            tint = if (isSelected) Color.White else SleekSlate400,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = style.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else SleekSlate400,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
