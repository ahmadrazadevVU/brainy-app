package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.QueryStats
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SettingsBrightness
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ThemeMode
import com.example.data.model.WellbeingStats
import com.example.ui.theme.BrainyTheme

@Composable
fun ProfileScreen(
    stats: WellbeingStats,
    onOpenTargetSetup: () -> Unit,
    onChangeTheme: (ThemeMode) -> Unit,
    onResetData: () -> Unit,
    onRestartOnboarding: () -> Unit,
    onOpenUsageSettings: () -> Unit = {},
    onOpenAccessibilitySettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors
    val scrollState = rememberScrollState()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showArchitectureDialog by remember { mutableStateOf(false) }
    var showTelemetryDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
            .testTag("profile_screen")
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Profile & Settings",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = colors.textPrimary
            )
        )
        Text(
            text = "Preferences and tracking parameters",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = colors.textSecondary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Identity Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(colors.brandIndigo.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "User Avatar",
                        tint = colors.brandIndigo,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Ahmad M.",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = colors.textPrimary
                        )
                    )
                    Text(
                        text = "Level 07 Digital Balancer • 320 XP",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "WELLBEING SETTINGS",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = colors.textSecondary,
                letterSpacing = 0.8.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Settings Group
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(20.dp))
        ) {
            Column {
                // Daily Target setting
                SettingsClickableRow(
                    icon = Icons.Rounded.TrackChanges,
                    title = "Daily Target",
                    subtitle = "${stats.targetScrolls} scrolls/day",
                    onClick = onOpenTargetSetup
                )

                HorizontalDivider(color = colors.border.copy(alpha = 0.5f))

                // Notifications Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = null,
                            tint = colors.brandIndigo,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Nudge Notifications",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = colors.textPrimary
                                )
                            )
                            Text(
                                text = "Gentle reminders when exceeding targets",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary
                                )
                            )
                        }
                    }

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                            checkedTrackColor = colors.brandIndigo
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Phase 2 Telemetry & Device Permissions Section
        Text(
            text = "DEVICE TELEMETRY & PERMISSIONS",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = colors.textSecondary,
                letterSpacing = 0.8.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(20.dp))
        ) {
            Column {
                SettingsClickableRow(
                    icon = Icons.Rounded.QueryStats,
                    title = "Usage Access (Screen Time & Sessions)",
                    subtitle = if (stats.isUsageAccessGranted) {
                        "Active (Verified) • ${stats.verifiedScreenTimeMinutes}m recorded"
                    } else {
                        "Tap to grant Usage Access in Settings"
                    },
                    onClick = onOpenUsageSettings
                )

                HorizontalDivider(color = colors.border.copy(alpha = 0.5f))

                SettingsClickableRow(
                    icon = Icons.Rounded.Accessibility,
                    title = "Scroll Detection (Accessibility)",
                    subtitle = if (stats.isAccessibilityActive) {
                        "Active (Verified) • ${stats.verifiedScrollEvents} scrolls captured"
                    } else {
                        "Tap to enable Brainy Service in Settings"
                    },
                    onClick = onOpenAccessibilitySettings
                )

                HorizontalDivider(color = colors.border.copy(alpha = 0.5f))

                SettingsClickableRow(
                    icon = Icons.Rounded.Analytics,
                    title = "Telemetry Data Distinction",
                    subtitle = "Inspect verified OS stats vs derived metrics",
                    onClick = { showTelemetryDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "APPEARANCE & THEME",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = colors.textSecondary,
                letterSpacing = 0.8.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Theme Mode Segmented Selector (System / Light / Dark)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ThemeSelectorChip(
                label = "System",
                icon = Icons.Rounded.SettingsBrightness,
                isSelected = stats.themeMode == ThemeMode.SYSTEM,
                onClick = { onChangeTheme(ThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f)
            )

            ThemeSelectorChip(
                label = "Light",
                icon = Icons.Rounded.LightMode,
                isSelected = stats.themeMode == ThemeMode.LIGHT,
                onClick = { onChangeTheme(ThemeMode.LIGHT) },
                modifier = Modifier.weight(1f)
            )

            ThemeSelectorChip(
                label = "Dark",
                icon = Icons.Rounded.DarkMode,
                isSelected = stats.themeMode == ThemeMode.DARK,
                onClick = { onChangeTheme(ThemeMode.DARK) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ARCHITECTURE & INFO",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = colors.textSecondary,
                letterSpacing = 0.8.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(20.dp))
        ) {
            Column {
                SettingsClickableRow(
                    icon = Icons.Rounded.Security,
                    title = "Phase 2 Tracking Architecture",
                    subtitle = "UsageStatsManager & AccessibilityService implementation",
                    onClick = { showArchitectureDialog = true }
                )

                HorizontalDivider(color = colors.border.copy(alpha = 0.5f))

                SettingsClickableRow(
                    icon = Icons.Rounded.RestartAlt,
                    title = "Replay Onboarding",
                    subtitle = "View welcome and target setup flow",
                    onClick = onRestartOnboarding
                )

                HorizontalDivider(color = colors.border.copy(alpha = 0.5f))

                SettingsClickableRow(
                    icon = Icons.Rounded.Info,
                    title = "About Brainy",
                    subtitle = "Version 2.0 (Phase 2 Native Tracking Active)",
                    onClick = { showAboutDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showTelemetryDialog) {
        AlertDialog(
            onDismissRequest = { showTelemetryDialog = false },
            title = {
                Text(
                    text = "Telemetry Metrics Breakdown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = colors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "1. VERIFIED DEVICE USAGE (UsageStatsManager)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = colors.brandIndigo
                    )
                    Text(
                        text = "• Foreground Time: ${stats.verifiedScreenTimeMinutes}m in short-video apps\n• Foreground Sessions: ${stats.verifiedSessionsCount} sessions\n• Status: ${if (stats.isUsageAccessGranted) "Permission Granted" else "Permission Required"}",
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "2. DETECTED SCROLL GESTURES (AccessibilityService)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = colors.brandIndigo
                    )
                    Text(
                        text = "• Verified Real Swipes: ${stats.verifiedScrollEvents} scrolls\n• Debounce Window: 450ms cooldown (1 physical swipe = 1 scroll)\n• Supported: Instagram Reels, TikTok, YouTube Shorts\n• Status: ${if (stats.isAccessibilityActive) "Active & Connected" else "Service Disabled/Offline"}",
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "3. DERIVED METRICS & RESILIENCE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = colors.brandIndigo
                    )
                    Text(
                        text = "• Current Display Total: ${stats.todayScrolls} / ${stats.targetScrolls}\n• Brain Reaction State: ${stats.brainState.title}\n• Device Reboot / App Unavailable: Gracefully maintains baseline without crashing or fabricating numbers.",
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTelemetryDialog = false }) {
                    Text("Close", color = colors.brandIndigo, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showArchitectureDialog) {
        AlertDialog(
            onDismissRequest = { showArchitectureDialog = false },
            title = {
                Text(
                    text = "Phase 2 Native Tracking Implementation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Brainy's Android tracking layer:\n\n• `UsageStatsTracker`: Queries UsageStatsManager for verified foreground short-video minutes and sessions.\n• `BrainyAccessibilityService`: Subscribes to TYPE_VIEW_SCROLLED events with a 450ms debouncer to accurately detect 1 swipe per short-video reel without duplicate micro-scrolls.\n• `AndroidTrackingService`: Implements `TrackingService` seamlessly. When permissions are denied or in emulator environments, it safely uses local baseline stats so UI never breaks.",
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showArchitectureDialog = false }) {
                    Text("Got it", color = colors.brandIndigo, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "Brainy • Your Digital Balance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Brainy is a premium gamified digital-wellbeing companion. Designed with an original emotional mascot, positive reinforcement, and mindful pacer mechanics.",
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = colors.brandIndigo, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = BrainyTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.brandIndigo,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = colors.textPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                )
            }
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ThemeSelectorChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colors.brandIndigo else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) androidx.compose.ui.graphics.Color.White else colors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (isSelected) androidx.compose.ui.graphics.Color.White else colors.textSecondary
                )
            )
        }
    }
}
