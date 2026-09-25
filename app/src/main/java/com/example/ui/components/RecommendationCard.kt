package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrainState
import com.example.ui.theme.BrainyTheme

@Composable
fun RecommendationCard(
    brainState: BrainState,
    onTakeBreakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(colors.recCardBg)
            .border(1.dp, colors.recCardBorder, RoundedCornerShape(22.dp))
            .padding(16.dp)
            .testTag("recommendation_card")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Mini Brain Mascot Circle
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = brainState.imageRes),
                            contentDescription = "Mini mascot",
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = brainState.recommendationTitle,
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = colors.textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = brainState.recommendationSubtitle,
                            style = androidx.compose.ui.text.TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        )
                    }
                }

                // Star Reward Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(colors.badgeGoldBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "★ +100 XP",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = colors.badgeGoldText
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Blue pill CTA Button: "☕ Take a Break"
            Button(
                onClick = onTakeBreakClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.brandIndigo
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("take_a_break_button")
            ) {
                Text(
                    text = "☕  Take a Break",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
        }
    }
}
