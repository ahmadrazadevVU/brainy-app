package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.WellbeingStats
import com.example.ui.theme.BrainyTheme

enum class InsightPeriod(val label: String) {
    TODAY("Today"),
    SEVEN_DAYS("7 Days"),
    THIRTY_DAYS("30 Days")
}

@Composable
fun InsightsScreen(
    stats: WellbeingStats,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors
    val scrollState = rememberScrollState()
    var selectedPeriod by remember { mutableStateOf(InsightPeriod.SEVEN_DAYS) }

    // Mock weekly scrolling dataset
    val weeklyData = listOf(
        "Mon" to 42,
        "Tue" to 78,
        "Wed" to 65,
        "Thu" to 92,
        "Fri" to 58,
        "Sat" to 110,
        "Sun" to 58
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
            .testTag("insights_screen")
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Insights",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = colors.textPrimary
            )
        )
        Text(
            text = "Patterns in your attention and focus rhythm",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = colors.textSecondary
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Time Period Segmented Control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(colors.surfaceSubtle)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InsightPeriod.entries.forEach { period ->
                val isSelected = selectedPeriod == period
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isSelected) colors.surface else Color.Transparent)
                        .clickable { selectedPeriod = period },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = period.label,
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (isSelected) colors.textPrimary else colors.textSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mascot Feedback Insight Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.recCardBg)
                .border(1.dp, colors.recCardBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.brainy_mascot_happy),
                    contentDescription = "Brainy Insight Mascot",
                    modifier = Modifier.size(46.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Brainy's Observation",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colors.brandIndigo
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "You've stayed below target 5 out of the last 7 days! Peak scrolling happens after 8 PM.",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = colors.textPrimary,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weekly Scroll Trend Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Scrolls",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = colors.textPrimary
                        )
                    )
                    Text(
                        text = "Avg 69 / day",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = colors.brandIndigo
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Compose Canvas Bar Chart
                val maxVal = 120f
                val brandColor = colors.brandIndigo
                val subtleColor = colors.surfaceSubtle
                val targetLineColor = colors.statPinkIcon.copy(alpha = 0.6f)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val barWidth = 24.dp.toPx()
                    val spacing = (size.width - (weeklyData.size * barWidth)) / (weeklyData.size + 1)
                    val chartHeight = size.height - 24.dp.toPx()

                    // Target reference line
                    val targetY = chartHeight - (stats.targetScrolls / maxVal * chartHeight)
                    drawLine(
                        color = targetLineColor,
                        start = Offset(0f, targetY),
                        end = Offset(size.width, targetY),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    weeklyData.forEachIndexed { index, pair ->
                        val x = spacing + index * (barWidth + spacing)
                        val barH = (pair.second / maxVal * chartHeight).coerceAtMost(chartHeight)
                        val y = chartHeight - barH

                        val isTargetExceeded = pair.second > stats.targetScrolls

                        // Background pillar
                        drawRoundRect(
                            color = subtleColor,
                            topLeft = Offset(x, 0f),
                            size = Size(barWidth, chartHeight),
                            cornerRadius = CornerRadius(12f, 12f)
                        )

                        // Filled bar
                        drawRoundRect(
                            color = if (isTargetExceeded) Color(0xFFF43F5E) else brandColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barH),
                            cornerRadius = CornerRadius(12f, 12f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Day Labels Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weeklyData.forEach { pair ->
                        Text(
                            text = pair.first,
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Short-Video App Breakdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "App Distribution",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colors.textPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                AppDistributionRow("Instagram Reels", "58 Scrolls (65%)", colors.statPinkIcon, 0.65f)
                Spacer(modifier = Modifier.height(10.dp))
                AppDistributionRow("TikTok", "20 Scrolls (22%)", colors.statCyanIcon, 0.22f)
                Spacer(modifier = Modifier.height(10.dp))
                AppDistributionRow("YouTube Shorts", "12 Scrolls (13%)", colors.statBlueIcon, 0.13f)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AppDistributionRow(
    appName: String,
    info: String,
    accentColor: Color,
    fraction: Float
) {
    val colors = BrainyTheme.colors
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = appName,
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = colors.textPrimary
                )
            )
            Text(
                text = info,
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(colors.surfaceSubtle)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(accentColor)
            )
        }
    }
}
