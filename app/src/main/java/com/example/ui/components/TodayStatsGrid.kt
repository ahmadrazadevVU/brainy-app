package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WellbeingStats
import com.example.ui.theme.BrainyTheme

@Composable
fun TodayStatsGrid(
    stats: WellbeingStats,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag("today_stats_section")
    ) {
        Text(
            text = "Today's Stats",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = colors.textPrimary
            ),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Instagram
            StatMiniCard(
                icon = Icons.Rounded.CameraAlt,
                iconColor = colors.statPinkIcon,
                bgColor = colors.statPinkBg,
                borderColor = colors.statPinkBorder,
                title = stats.activeAppName,
                primaryValue = "${stats.todayScrolls}",
                secondaryLabel = "Scrolls",
                modifier = Modifier.weight(1f)
            )

            // Card 2: Screen Time
            StatMiniCard(
                icon = Icons.Rounded.Schedule,
                iconColor = colors.statBlueIcon,
                bgColor = colors.statBlueBg,
                borderColor = colors.statBlueBorder,
                title = "Screen Time",
                primaryValue = stats.screenTimeFormatted,
                secondaryLabel = null,
                modifier = Modifier.weight(1f)
            )

            // Card 3: Sessions
            StatMiniCard(
                icon = Icons.Rounded.BarChart,
                iconColor = colors.statCyanIcon,
                bgColor = colors.statCyanBg,
                borderColor = colors.statCyanBorder,
                title = "Sessions",
                primaryValue = "${stats.sessionsCount}",
                secondaryLabel = null,
                modifier = Modifier.weight(1f)
            )

            // Card 4: XP
            StatMiniCard(
                icon = Icons.Rounded.Star,
                iconColor = colors.statAmberIcon,
                bgColor = colors.statAmberBg,
                borderColor = colors.statAmberBorder,
                title = "XP",
                primaryValue = "+${stats.currentXp}",
                secondaryLabel = null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatMiniCard(
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    borderColor: Color,
    title: String,
    primaryValue: String,
    secondaryLabel: String?,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors

    Box(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Mini Icon Circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = colors.textSecondary
                ),
                maxLines = 1
            )

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = primaryValue,
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colors.textPrimary
                    )
                )
                if (secondaryLabel != null) {
                    Text(
                        text = " $secondaryLabel",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            color = colors.textSecondary
                        )
                    )
                }
            }
        }
    }
}
