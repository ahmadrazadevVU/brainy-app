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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WellbeingStats
import com.example.ui.theme.BrainyTheme

data class JourneyMilestone(
    val level: Int,
    val title: String,
    val perk: String,
    val isUnlocked: Boolean,
    val isCurrent: Boolean = false
)

@Composable
fun JourneyScreen(
    stats: WellbeingStats,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors
    val scrollState = rememberScrollState()

    val milestones = listOf(
        JourneyMilestone(1, "Sprout Mind", "Unlocked Brainy base emotional reactions", isUnlocked = true),
        JourneyMilestone(3, "Focus Spark", "Customized daily scroll vibration feedback", isUnlocked = true),
        JourneyMilestone(5, "Pacing Novice", "Unlocked 5-minute breathing reset timer", isUnlocked = true),
        JourneyMilestone(7, "Digital Balancer", "Exclusive Dizzy Star Mascot aura (Active)", isUnlocked = true, isCurrent = true),
        JourneyMilestone(10, "Zen Master", "Unlocks Zen Forest dark theme mode", isUnlocked = false),
        JourneyMilestone(15, "Scroll Ninja", "Unlocks automatic evening feed cooldown", isUnlocked = false)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
            .testTag("journey_screen")
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Journey",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = colors.textPrimary
            )
        )
        Text(
            text = "Your path toward effortless digital freedom",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = colors.textSecondary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Big Level Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT STAGE",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = colors.brandIndigo,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Level 07: Digital Balancer",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.textPrimary
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(colors.badgeGoldBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Milestone",
                            tint = colors.badgeGoldText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { stats.levelProgressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50.dp)),
                    color = colors.brandIndigo,
                    trackColor = colors.surfaceSubtle,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${stats.currentLevelXp} / ${stats.levelTargetXp} XP",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    )
                    Text(
                        text = "${stats.xpToNextReward} XP to Level 08",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = colors.brandIndigo
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Progression Milestones",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colors.textPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Visual Roadmap Path
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            milestones.forEach { milestone ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (milestone.isCurrent) colors.recCardBg else colors.surface
                        )
                        .border(
                            1.dp,
                            if (milestone.isCurrent) colors.brandIndigo else colors.border,
                            RoundedCornerShape(18.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        milestone.isCurrent -> colors.brandIndigo
                                        milestone.isUnlocked -> colors.brandIndigo.copy(alpha = 0.15f)
                                        else -> colors.surfaceSubtle
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    milestone.isUnlocked -> Icons.Rounded.CheckCircle
                                    else -> Icons.Rounded.Lock
                                },
                                contentDescription = null,
                                tint = when {
                                    milestone.isCurrent -> androidx.compose.ui.graphics.Color.White
                                    milestone.isUnlocked -> colors.brandIndigo
                                    else -> colors.textMuted
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LVL ${milestone.level} • ${milestone.title}",
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = colors.textPrimary
                                    )
                                )
                                if (milestone.isCurrent) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(colors.brandIndigo)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "CURRENT",
                                            style = androidx.compose.ui.text.TextStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = androidx.compose.ui.graphics.Color.White
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = milestone.perk,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
