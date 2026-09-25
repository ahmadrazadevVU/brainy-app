package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlatformStats
import com.example.data.model.ShortVideoPlatform
import com.example.ui.theme.BrainyTheme

@Composable
fun YourScrollsSection(
    platformStats: PlatformStats,
    targetScrolls: Int,
    onPlatformClick: (ShortVideoPlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag("your_scrolls_section")
    ) {
        // Section Header with Total Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your Scrolls",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = colors.textPrimary
                    )
                )
                Text(
                    text = "Tracked across short-video feeds",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                )
            }

            // Total pill tag: e.g. "76 total"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(colors.surfaceSubtle)
                    .border(1.dp, colors.border, RoundedCornerShape(50.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
                    .testTag("platform_total_pill")
            ) {
                Text(
                    text = "${platformStats.totalScrolls} total",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colors.brandIndigo
                    )
                )
            }
        }

        // 4 Compact Platform Rows in a unified Brainy container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PlatformRowItem(
                    platform = ShortVideoPlatform.INSTAGRAM,
                    count = platformStats.instagram,
                    target = targetScrolls,
                    onClick = { onPlatformClick(ShortVideoPlatform.INSTAGRAM) },
                    testTag = "platform_item_instagram"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.border.copy(alpha = 0.5f))
                )

                PlatformRowItem(
                    platform = ShortVideoPlatform.TIKTOK,
                    count = platformStats.tiktok,
                    target = targetScrolls,
                    onClick = { onPlatformClick(ShortVideoPlatform.TIKTOK) },
                    testTag = "platform_item_tiktok"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.border.copy(alpha = 0.5f))
                )

                PlatformRowItem(
                    platform = ShortVideoPlatform.YOUTUBE,
                    count = platformStats.youtube,
                    target = targetScrolls,
                    onClick = { onPlatformClick(ShortVideoPlatform.YOUTUBE) },
                    testTag = "platform_item_youtube"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.border.copy(alpha = 0.5f))
                )

                PlatformRowItem(
                    platform = ShortVideoPlatform.SNAPCHAT,
                    count = platformStats.snapchat,
                    target = targetScrolls,
                    onClick = { onPlatformClick(ShortVideoPlatform.SNAPCHAT) },
                    testTag = "platform_item_snapchat"
                )
            }
        }
    }
}

@Composable
private fun PlatformRowItem(
    platform: ShortVideoPlatform,
    count: Int,
    target: Int,
    onClick: () -> Unit,
    testTag: String
) {
    val colors = BrainyTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "platform_row_scale"
    )

    val progressFraction = (count.toFloat() / target.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val accentColor = Color(platform.accentHex)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Small recognizable platform mark
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = platform.markText,
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = if (colors.isDark) Color.White else accentColor
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Platform Name & progress bar
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = platform.displayName,
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = colors.textPrimary
                    )
                )

                Text(
                    text = "$count",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colors.textPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tiny progress indicator
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50.dp)),
                color = colors.brandIndigo,
                trackColor = colors.surfaceSubtle,
                strokeCap = StrokeCap.Round
            )
        }
    }
}
