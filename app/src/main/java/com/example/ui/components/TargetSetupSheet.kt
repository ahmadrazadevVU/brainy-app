package com.example.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetSetupSheet(
    currentTarget: Int,
    onSaveTarget: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val colors = BrainyTheme.colors
    var sliderValue by remember { mutableFloatStateOf(currentTarget.toFloat()) }
    val displayTarget = sliderValue.roundToInt()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .testTag("target_setup_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary
                    )
                }
            }

            Text(
                text = "How many scrolls feel right for you?",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colors.textPrimary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Large Animated Number
            val animatedCount by animateIntAsState(
                targetValue = displayTarget,
                animationSpec = tween(150),
                label = "target_num_anim"
            )

            Text(
                text = "$animatedCount",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Black,
                    fontSize = 64.sp,
                    color = colors.brandIndigo,
                    letterSpacing = (-1.5).sp
                )
            )

            Text(
                text = "scrolls per day",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = colors.textSecondary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Refined Slider (range 50 to 250)
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 50f..250f,
                steps = 19, // steps of 10
                colors = SliderDefaults.colors(
                    thumbColor = colors.brandIndigo,
                    activeTrackColor = colors.brandIndigo,
                    inactiveTrackColor = colors.surfaceSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("target_slider")
            )

            Spacer(modifier = Modifier.height(14.dp))

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
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
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
                                fontSize = 13.sp,
                                color = if (isSelected) androidx.compose.ui.graphics.Color.White else colors.textPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your target can be changed anytime.",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSaveTarget(displayTarget) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.brandIndigo
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("set_my_target_button")
            ) {
                Text(
                    text = "Set My Target",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
