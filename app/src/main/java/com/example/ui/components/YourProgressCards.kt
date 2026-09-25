package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WellbeingStats
import com.example.ui.theme.BrainyTheme

@Composable
fun YourProgressCards(
    stats: WellbeingStats,
    onQuestClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag("your_progress_section")
    ) {
        Text(
            text = "Your Progress",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = colors.textPrimary
            ),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Level Progression Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shield Badge with "LEVEL 07"
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.brandIndigo.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LEVEL",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                color = colors.brandIndigo
                            )
                        )
                        Text(
                            text = String.format("%02d", stats.level),
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = colors.brandIndigo
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Level ${String.format("%02d", stats.level)}",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = colors.textPrimary
                            )
                        )
                        Text(
                            text = "${stats.currentLevelXp} / ${stats.levelTargetXp} XP",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val animatedLevelProgress by animateFloatAsState(
                        targetValue = stats.levelProgressFraction,
                        animationSpec = tween(600),
                        label = "level_progress_anim"
                    )

                    LinearProgressIndicator(
                        progress = { animatedLevelProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        color = colors.brandIndigo,
                        trackColor = colors.surfaceSubtle,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next reward in ${stats.xpToNextReward} XP",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        )
                        Icon(
                            imageVector = Icons.Rounded.CardGiftcard,
                            contentDescription = "Reward",
                            tint = colors.brandIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Daily Quest Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                .clickable { onQuestClick() }
                .padding(16.dp)
                .testTag("daily_quest_card")
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // Bullseye Target Icon in circle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(colors.brandIndigo.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GpsFixed,
                        contentDescription = "Daily Quest",
                        tint = colors.brandIndigo,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Quest",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(colors.badgeGoldBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "★ +100 XP",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = colors.badgeGoldText
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Complete a 5 minute break",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colors.textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Take a real break. Your brain will thank you.",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val questFraction = (stats.breakQuestCompleted.toFloat() / stats.breakQuestTotal.toFloat()).coerceIn(0f, 1f)
                        val animatedQuestProgress by animateFloatAsState(
                            targetValue = questFraction,
                            animationSpec = tween(600),
                            label = "quest_progress_anim"
                        )

                        LinearProgressIndicator(
                            progress = { animatedQuestProgress },
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            color = colors.brandIndigo,
                            trackColor = colors.surfaceSubtle,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "${stats.breakQuestCompleted} / ${stats.breakQuestTotal}",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        )
                    }
                }
            }
        }
    }
}
