package com.example.ui.screens

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrainyTheme
import kotlin.math.roundToInt

@Composable
fun TargetSetupScreen(
    initialTarget: Int = 100,
    onTargetConfirmed: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors
    var sliderValue by remember { mutableFloatStateOf(initialTarget.toFloat()) }
    val displayTarget = sliderValue.roundToInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 28.dp, vertical = 32.dp)
            .testTag("target_setup_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How many scrolls feel right for you?",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    color = colors.textPrimary,
                    letterSpacing = (-0.3).sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Very Large Number
            val animatedTarget by animateIntAsState(
                targetValue = displayTarget,
                animationSpec = tween(150),
                label = "target_setup_num"
            )

            Text(
                text = "$animatedTarget",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Black,
                    fontSize = 80.sp,
                    lineHeight = 84.sp,
                    color = colors.brandIndigo,
                    letterSpacing = (-2).sp
                ),
                modifier = Modifier.testTag("target_number_label")
            )

            Text(
                text = "scrolls per day",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = colors.textSecondary
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Refined Slider (Range 50 - 250)
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 50f..250f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = colors.brandIndigo,
                    activeTrackColor = colors.brandIndigo,
                    inactiveTrackColor = colors.surfaceSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("target_slider_control")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Choice Chips: 50, 100, 150, 200
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(50, 100, 150, 200).forEach { choice ->
                    val isSelected = displayTarget == choice
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) colors.brandIndigo else colors.surfaceSubtle
                            )
                            .clickable { sliderValue = choice.toFloat() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$choice",
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (isSelected) androidx.compose.ui.graphics.Color.White else colors.textPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your target can be changed anytime.",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = colors.textMuted
                )
            )
        }

        // Primary CTA: Set My Target
        Button(
            onClick = { onTargetConfirmed(displayTarget) },
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.brandIndigo
            ),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("set_target_cta_button")
        ) {
            Text(
                text = "Set My Target",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    }
}
