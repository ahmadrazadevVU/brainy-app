package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrainyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InteractiveTesterSheet(
    todayScrolls: Int,
    target: Int,
    onAddScrolls: (Int) -> Unit,
    onSetScrolls: (Int) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val colors = BrainyTheme.colors

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
                .testTag("interactive_tester_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Demo Simulation Controls",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.textPrimary
                        )
                    )
                    Text(
                        text = "Test Brainy's emotional reaction thresholds live",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    )
                }

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

            Spacer(modifier = Modifier.height(16.dp))

            // Current State Summary Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceSubtle)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current: $todayScrolls / $target Scrolls",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colors.textPrimary
                        )
                    )

                    OutlinedButton(
                        onClick = onReset,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Reset",
                            modifier = Modifier.size(14.dp),
                            tint = colors.textSecondary
                        )
                        Text(
                            text = " Reset",
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "QUICK STATE JUMPS",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = colors.textSecondary,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Preset state buttons
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip("Happy (20)", 20, todayScrolls, onSetScrolls)
                PresetChip("Dizzy (58)", 58, todayScrolls, onSetScrolls)
                PresetChip("Target (${target})", target, todayScrolls, onSetScrolls)
                PresetChip("Tired (${target + 20})", target + 20, todayScrolls, onSetScrolls)
                PresetChip("Overloaded (${target + 40})", target + 40, todayScrolls, onSetScrolls)
                PresetChip("Strong (${target + 60})", target + 60, todayScrolls, onSetScrolls)
                PresetChip("Break (${target + 100})", target + 100, todayScrolls, onSetScrolls)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SIMULATE SCROLL EVENTS",
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = colors.textSecondary,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onAddScrolls(1) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceSubtle),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("+1", color = colors.textPrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onAddScrolls(5) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceSubtle),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("+5", color = colors.textPrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onAddScrolls(20) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.brandIndigo),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("+20", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetChip(
    label: String,
    scrollsValue: Int,
    currentScrolls: Int,
    onSelect: (Int) -> Unit
) {
    val colors = BrainyTheme.colors
    val isSelected = currentScrolls == scrollsValue

    FilterChip(
        selected = isSelected,
        onClick = { onSelect(scrollsValue) },
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.brandIndigo.copy(alpha = 0.15f),
            selectedLabelColor = colors.brandIndigo,
            containerColor = colors.surfaceSubtle,
            labelColor = colors.textPrimary
        ),
        shape = RoundedCornerShape(50.dp)
    )
}
