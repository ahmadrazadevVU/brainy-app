package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrainState
import com.example.ui.theme.BrainyTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BrainMascotHero(
    brainState: BrainState,
    todayScrolls: Int,
    targetScrolls: Int,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors

    // Subtle Breathing / Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "hero_mascot_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    )

    // Wobble animation when near/over target
    val wobbleAngle by infiniteTransition.animateFloat(
        initialValue = if (brainState.isAlertOrHigher) -3.5f else 0f,
        targetValue = if (brainState.isAlertOrHigher) 3.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble_angle"
    )

    // Orbiting rotation angle for stars
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("mascot_hero_section"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Mascot character with animations
        Box(
            modifier = Modifier
                .size(175.dp)
                .offset { IntOffset(0, floatOffset.toInt()) }
                .rotate(wobbleAngle),
            contentAlignment = Alignment.Center
        ) {
            // Mascot Base Image
            Image(
                painter = painterResource(id = brainState.imageRes),
                contentDescription = "Brainy Mascot - ${brainState.title}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(165.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("mascot_image")
            )

            // Dynamic Canvas overlay for Orbiting Stars & Motion Ticks
            if (brainState.showOrbitingStars) {
                Canvas(modifier = Modifier.size(175.dp)) {
                    val centerX = size.width / 2f
                    val centerY = size.height * 0.18f
                    val radiusX = size.width * 0.42f
                    val radiusY = size.height * 0.14f

                    // Subtle cyan orbital ring
                    drawOval(
                        color = Color(0x6638BDF8),
                        topLeft = Offset(centerX - radiusX, centerY - radiusY),
                        size = androidx.compose.ui.geometry.Size(radiusX * 2, radiusY * 2),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // 4 Orbiting Golden Stars
                    val starAngles = listOf(0.0, 90.0, 180.0, 270.0)
                    for (baseDeg in starAngles) {
                        val currentRad = Math.toRadians(orbitAngle.toDouble() + baseDeg)
                        val starX = centerX + radiusX * cos(currentRad).toFloat()
                        val starY = centerY + radiusY * sin(currentRad).toFloat()
                        val depthScale = 0.7f + 0.3f * sin(currentRad).toFloat()

                        // Draw golden 4-pointed star
                        drawStar(
                            center = Offset(starX, starY),
                            radius = 6.dp.toPx() * depthScale,
                            color = Color(0xFFFBBF24)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Right side: Metrics and status
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            // Pill tag: "TODAY'S SCROLLS"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(colors.surfaceSubtle)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
                    .testTag("todays_scrolls_badge")
            ) {
                Text(
                    text = "TODAY'S SCROLLS",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp,
                        color = colors.textSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Large scroll number with speed ticks
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$todayScrolls",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Black,
                        fontSize = 52.sp,
                        lineHeight = 54.sp,
                        color = colors.textPrimary,
                        letterSpacing = (-1.5).sp
                    ),
                    modifier = Modifier.testTag("scroll_count_display")
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Cute Motion ticks (3 short diagonal accent lines)
                Canvas(modifier = Modifier.size(18.dp, 28.dp)) {
                    val strokeW = 2.5.dp.toPx()
                    val tickColor = colors.textPrimary.copy(alpha = 0.85f)
                    drawLine(
                        color = tickColor,
                        start = Offset(0f, 6.dp.toPx()),
                        end = Offset(12.dp.toPx(), 2.dp.toPx()),
                        strokeWidth = strokeW
                    )
                    drawLine(
                        color = tickColor,
                        start = Offset(0f, 14.dp.toPx()),
                        end = Offset(16.dp.toPx(), 14.dp.toPx()),
                        strokeWidth = strokeW
                    )
                    drawLine(
                        color = tickColor,
                        start = Offset(0f, 22.dp.toPx()),
                        end = Offset(12.dp.toPx(), 26.dp.toPx()),
                        strokeWidth = strokeW
                    )
                }
            }

            Text(
                text = "scrolls",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Dynamic emotional message
            Text(
                text = brainState.headline,
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    lineHeight = 17.sp
                ),
                modifier = Modifier.testTag("brain_state_message")
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path()
    val innerRadius = radius * 0.4f
    for (i in 0 until 8) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = (i * PI / 4).toFloat() - (PI / 2).toFloat()
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color)
}
