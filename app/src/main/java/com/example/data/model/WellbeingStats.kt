package com.example.data.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

/**
 * Encapsulates both presentation state and verified Android device metrics.
 *
 * Guarantees:
 * 1. Platform-separated metrics for Instagram, TikTok, YouTube Shorts, and Snapchat.
 * 2. `todayScrolls` reflects total platform scrolls (34 + 22 + 13 + 7 = 76).
 * 3. Strict separation of verified device telemetry from derived metrics.
 */
data class WellbeingStats(
    val targetScrolls: Int = 100,
    val platformStats: PlatformStats = PlatformStats(),
    val todayScrolls: Int = platformStats.totalScrolls,
    val screenTimeMinutes: Int = 0,
    val sessionsCount: Int = 0,
    val currentXp: Int = 0,
    val level: Int = 1,
    val currentLevelXp: Int = 0,
    val levelTargetXp: Int = 1000,
    val breakQuestCompleted: Int = 0,
    val breakQuestTotal: Int = 5,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isOnboardingCompleted: Boolean = false,
    val activeAppName: String = "Instagram",

    // Verified Android telemetry distinctions:
    val isUsageAccessGranted: Boolean = false,
    val isAccessibilityActive: Boolean = false,
    val verifiedScrollEvents: Int = 0,
    val verifiedScreenTimeMinutes: Int = 0,
    val verifiedSessionsCount: Int = 0,
    val isLiveTrackingActive: Boolean = false,
    val isDemoSimulationActive: Boolean = false
) {
    val screenTimeFormatted: String
        get() {
            val hours = screenTimeMinutes / 60
            val minutes = screenTimeMinutes % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }

    val xpToNextReward: Int
        get() = (levelTargetXp - currentLevelXp).coerceAtLeast(0)

    val progressFraction: Float
        get() = (todayScrolls.toFloat() / targetScrolls.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    val levelProgressFraction: Float
        get() = (currentLevelXp.toFloat() / levelTargetXp.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    val brainState: BrainState
        get() = BrainState.fromScrolls(todayScrolls, targetScrolls)
}
