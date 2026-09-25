package com.example.data.service

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import com.example.data.model.BrainState
import com.example.data.model.PlatformStats
import com.example.data.model.ThemeMode
import com.example.data.model.WellbeingStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Production-ready Android implementation of TrackingService.
 *
 * Integrates real hardware and OS telemetry:
 * 1. Multi-platform support: Instagram Reels, TikTok, YouTube Shorts, Snapchat Spotlight.
 * 2. UsageStatsManager for verified short-video foreground usage & sessions.
 * 3. BrainyAccessibilityService for real short-video scroll-gesture detection.
 * 4. Strict separation of real device telemetry from demo/simulation mode.
 * 5. Clean fresh-install zero-state initialization and local date rollover.
 */
class AndroidTrackingService(
    private val context: Context,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : TrackingService, BrainyAccessibilityService.ScrollEventListener {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("brainy_wellbeing_prefs", Context.MODE_PRIVATE)

    private val usageStatsTracker = UsageStatsTracker(context)

    // True hardware/device platform stats, persisted locally
    private var realPlatformStats = PlatformStats()
    private var isDemoSimulationActive = false

    private val _stats = MutableStateFlow(loadPersistedStats())
    override val stats: StateFlow<WellbeingStats> = _stats.asStateFlow()

    private val _thresholdCrossedEvents = MutableSharedFlow<BrainState>(extraBufferCapacity = 1)
    override val thresholdCrossedEvents: SharedFlow<BrainState> = _thresholdCrossedEvents.asSharedFlow()

    init {
        BrainyAccessibilityService.addListener(this)
        checkAndPerformMidnightRollover()
        refreshDeviceMetrics()
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun loadPersistedStats(): WellbeingStats {
        val todayDateStr = getTodayDateString()
        val storedDate = prefs.getString("trackingDate", null)

        val (ig, tt, yt, sc) = if (storedDate == todayDateStr) {
            listOf(
                prefs.getInt("instagramTodayScrolls", 0),
                prefs.getInt("tiktokTodayScrolls", 0),
                prefs.getInt("youtubeTodayScrolls", 0),
                prefs.getInt("snapchatTodayScrolls", 0)
            )
        } else {
            // First launch or calendar date rolled over: clean zero state
            prefs.edit()
                .putString("trackingDate", todayDateStr)
                .putInt("instagramTodayScrolls", 0)
                .putInt("tiktokTodayScrolls", 0)
                .putInt("youtubeTodayScrolls", 0)
                .putInt("snapchatTodayScrolls", 0)
                .putInt("today_scrolls", 0)
                .putInt("verified_scrolls", 0)
                .remove("platform_ig")
                .remove("platform_tt")
                .remove("platform_yt")
                .remove("platform_sc")
                .apply()
            listOf(0, 0, 0, 0)
        }

        realPlatformStats = PlatformStats(
            instagram = ig,
            tiktok = tt,
            youtube = yt,
            snapchat = sc
        )

        val target = prefs.getInt("target_scrolls", 100)
        val screenTime = prefs.getInt("screen_time_mins", 0)
        val sessions = prefs.getInt("sessions_count", 0)
        val xp = prefs.getInt("current_xp", 0)
        val level = prefs.getInt("level", 1)
        val levelXp = prefs.getInt("level_xp", 0)
        val levelTargetXp = prefs.getInt("level_target_xp", 1000)
        val breakQuest = prefs.getInt("break_quest_completed", 0)
        val themeOrdinal = prefs.getInt("theme_mode", ThemeMode.SYSTEM.ordinal)
        val themeMode = ThemeMode.entries.getOrElse(themeOrdinal) { ThemeMode.SYSTEM }
        val onboardingDone = prefs.getBoolean("onboarding_completed", true)
        val verifiedScrolls = prefs.getInt("verified_scrolls", 0)

        val isUsageGranted = usageStatsTracker.isUsageAccessGranted()
        val isA11yActive = isAccessibilityServiceEnabled()

        return WellbeingStats(
            targetScrolls = target,
            platformStats = realPlatformStats,
            todayScrolls = realPlatformStats.totalScrolls,
            screenTimeMinutes = screenTime,
            sessionsCount = sessions,
            currentXp = xp,
            level = level,
            currentLevelXp = levelXp,
            levelTargetXp = levelTargetXp,
            breakQuestCompleted = breakQuest,
            themeMode = themeMode,
            isOnboardingCompleted = onboardingDone,
            isUsageAccessGranted = isUsageGranted,
            isAccessibilityActive = isA11yActive,
            verifiedScrollEvents = verifiedScrolls,
            isLiveTrackingActive = isUsageGranted || isA11yActive,
            isDemoSimulationActive = false
        )
    }

    private fun persistStats(stats: WellbeingStats) {
        val todayDateStr = getTodayDateString()
        prefs.edit()
            .putString("trackingDate", todayDateStr)
            .putInt("instagramTodayScrolls", realPlatformStats.instagram)
            .putInt("tiktokTodayScrolls", realPlatformStats.tiktok)
            .putInt("youtubeTodayScrolls", realPlatformStats.youtube)
            .putInt("snapchatTodayScrolls", realPlatformStats.snapchat)
            .putInt("today_scrolls", realPlatformStats.totalScrolls)
            .putInt("target_scrolls", stats.targetScrolls)
            .putInt("screen_time_mins", stats.screenTimeMinutes)
            .putInt("sessions_count", stats.sessionsCount)
            .putInt("current_xp", stats.currentXp)
            .putInt("level", stats.level)
            .putInt("level_xp", stats.currentLevelXp)
            .putInt("level_target_xp", stats.levelTargetXp)
            .putInt("break_quest_completed", stats.breakQuestCompleted)
            .putInt("theme_mode", stats.themeMode.ordinal)
            .putBoolean("onboarding_completed", stats.isOnboardingCompleted)
            .putInt("verified_scrolls", stats.verifiedScrollEvents)
            .apply()
    }

    private fun checkAndPerformMidnightRollover() {
        val todayDateStr = getTodayDateString()
        val storedDate = prefs.getString("trackingDate", null)

        if (storedDate != null && storedDate != todayDateStr) {
            // Day rolled over: reset daily counts safely
            val emptyPlatforms = PlatformStats(0, 0, 0, 0)
            realPlatformStats = emptyPlatforms
            prefs.edit()
                .putString("trackingDate", todayDateStr)
                .putInt("instagramTodayScrolls", 0)
                .putInt("tiktokTodayScrolls", 0)
                .putInt("youtubeTodayScrolls", 0)
                .putInt("snapchatTodayScrolls", 0)
                .putInt("today_scrolls", 0)
                .putInt("verified_scrolls", 0)
                .putInt("screen_time_mins", 0)
                .putInt("sessions_count", 0)
                .apply()

            if (!isDemoSimulationActive) {
                _stats.value = _stats.value.copy(
                    platformStats = emptyPlatforms,
                    todayScrolls = 0,
                    verifiedScrollEvents = 0,
                    screenTimeMinutes = 0,
                    sessionsCount = 0
                )
            }
        }
    }

    fun refreshDeviceMetrics() {
        checkAndPerformMidnightRollover()
        val isUsageGranted = usageStatsTracker.isUsageAccessGranted()
        val isA11yActive = isAccessibilityServiceEnabled()
        val current = _stats.value

        var updatedMinutes = current.screenTimeMinutes
        var updatedSessions = current.sessionsCount
        var verifiedMins = 0
        var verifiedSessions = 0
        var dominantApp = current.activeAppName

        if (isUsageGranted) {
            val usage = usageStatsTracker.queryTodayUsage()
            if (usage.totalForegroundMinutes > 0) {
                verifiedMins = usage.totalForegroundMinutes
                verifiedSessions = usage.sessionsCount
                updatedMinutes = usage.totalForegroundMinutes
                updatedSessions = usage.sessionsCount
                dominantApp = usage.dominantAppName
            }
        }

        val newStats = current.copy(
            isUsageAccessGranted = isUsageGranted,
            isAccessibilityActive = isA11yActive,
            screenTimeMinutes = updatedMinutes,
            sessionsCount = updatedSessions,
            verifiedScreenTimeMinutes = verifiedMins,
            verifiedSessionsCount = verifiedSessions,
            activeAppName = dominantApp,
            isLiveTrackingActive = isUsageGranted || isA11yActive
        )
        _stats.value = newStats
        persistStats(newStats)
    }

    // BrainyAccessibilityService.ScrollEventListener implementation
    override fun onScrollDetected(packageName: String, eventTime: Long) {
        checkAndPerformMidnightRollover()

        val oldState = _stats.value.brainState
        val currentReal = realPlatformStats

        val (platformKey, updatedRealPlatforms) = when (packageName) {
            "com.instagram.android" -> Pair("instagram", currentReal.copy(instagram = currentReal.instagram + 1))
            "com.zhiliaoapp.musically", "com.ss.android.ugc.trill" -> Pair("tiktok", currentReal.copy(tiktok = currentReal.tiktok + 1))
            "com.google.android.youtube" -> Pair("youtube", currentReal.copy(youtube = currentReal.youtube + 1))
            "com.snapchat.android" -> Pair("snapchat", currentReal.copy(snapchat = currentReal.snapchat + 1))
            else -> Pair("instagram", currentReal.copy(instagram = currentReal.instagram + 1))
        }

        realPlatformStats = updatedRealPlatforms
        val newVerified = _stats.value.verifiedScrollEvents + 1

        // Requirement 15: Concise debug logging for verified scrolls
        Log.d("BrainyTracking", "platform=$platformKey\nevent=scroll\ncount=1")
        Log.d("BrainyTracking", "instagram=${updatedRealPlatforms.instagram}\ntiktok=${updatedRealPlatforms.tiktok}\nyoutube=${updatedRealPlatforms.youtube}\nsnapchat=${updatedRealPlatforms.snapchat}\ntotal=${updatedRealPlatforms.totalScrolls}")

        val appName = UsageStatsTracker.TARGET_PACKAGES[packageName] ?: _stats.value.activeAppName

        val newStats = if (isDemoSimulationActive) {
            // Maintain active presentation simulation on screen while safely persisting real counts in background
            _stats.value.copy(
                verifiedScrollEvents = newVerified,
                activeAppName = appName,
                isAccessibilityActive = true
            )
        } else {
            _stats.value.copy(
                platformStats = updatedRealPlatforms,
                todayScrolls = updatedRealPlatforms.totalScrolls,
                verifiedScrollEvents = newVerified,
                activeAppName = appName,
                isAccessibilityActive = true
            )
        }

        _stats.value = newStats
        persistStats(newStats)
        checkThresholdChange(oldState, newStats.brainState)
    }

    override fun onServiceStatusChanged(isConnected: Boolean) {
        val current = _stats.value
        val newStats = current.copy(
            isAccessibilityActive = isConnected,
            isLiveTrackingActive = current.isUsageAccessGranted || isConnected
        )
        _stats.value = newStats
        persistStats(newStats)
    }

    override fun setTarget(target: Int) {
        val oldState = _stats.value.brainState
        val newStats = _stats.value.copy(targetScrolls = target)
        _stats.value = newStats
        persistStats(newStats)
        checkThresholdChange(oldState, newStats.brainState)
    }

    override fun recordScrollDelta(delta: Int) {
        val currentStats = _stats.value
        val newScrolls = (currentStats.todayScrolls + delta).coerceAtLeast(0)
        if (newScrolls == 0) {
            resetDemoSimulation()
            return
        }
        setScrollsDirectly(newScrolls)
    }

    override fun setScrollsDirectly(count: Int) {
        val safeCount = count.coerceAtLeast(0)
        if (safeCount == 0) {
            resetDemoSimulation()
            return
        }

        // Explicit demo tester activation: isolate from real tracking
        isDemoSimulationActive = true
        val oldState = _stats.value.brainState

        val ig = (safeCount * 0.45).toInt()
        val tt = (safeCount * 0.30).toInt()
        val yt = (safeCount * 0.15).toInt()
        val sc = safeCount - (ig + tt + yt)
        val demoPlatforms = PlatformStats(ig, tt, yt, sc)

        val newStats = _stats.value.copy(
            platformStats = demoPlatforms,
            todayScrolls = demoPlatforms.totalScrolls,
            isDemoSimulationActive = true
        )
        _stats.value = newStats
        checkThresholdChange(oldState, newStats.brainState)
    }

    override fun resetDemoSimulation() {
        val oldState = _stats.value.brainState
        isDemoSimulationActive = false
        val newStats = _stats.value.copy(
            platformStats = realPlatformStats,
            todayScrolls = realPlatformStats.totalScrolls,
            isDemoSimulationActive = false
        )
        _stats.value = newStats
        checkThresholdChange(oldState, newStats.brainState)
    }

    override fun completeBreakSession(minutes: Int) {
        val current = _stats.value
        val xpGain = 100
        val newTotalXp = current.currentXp + xpGain
        val newLevelXp = current.currentLevelXp + xpGain
        val newQuestCompleted = (current.breakQuestCompleted + 1).coerceAtMost(current.breakQuestTotal)

        val (finalLevel, finalLevelXp) = if (newLevelXp >= current.levelTargetXp) {
            Pair(current.level + 1, newLevelXp - current.levelTargetXp)
        } else {
            Pair(current.level, newLevelXp)
        }

        val newStats = current.copy(
            currentXp = newTotalXp,
            level = finalLevel,
            currentLevelXp = finalLevelXp,
            breakQuestCompleted = newQuestCompleted
        )
        _stats.value = newStats
        persistStats(newStats)

        externalScope.launch {
            _thresholdCrossedEvents.emit(BrainState.CELEBRATING)
        }
    }

    override fun resetDaily() {
        isDemoSimulationActive = false
        val emptyPlatforms = PlatformStats(0, 0, 0, 0)
        realPlatformStats = emptyPlatforms
        val newStats = _stats.value.copy(
            platformStats = emptyPlatforms,
            todayScrolls = 0,
            verifiedScrollEvents = 0,
            screenTimeMinutes = 0,
            sessionsCount = 0,
            isDemoSimulationActive = false
        )
        _stats.value = newStats
        persistStats(newStats)
    }

    override fun setThemeMode(mode: ThemeMode) {
        val newStats = _stats.value.copy(themeMode = mode)
        _stats.value = newStats
        persistStats(newStats)
    }

    override fun setOnboardingCompleted(completed: Boolean) {
        val newStats = _stats.value.copy(isOnboardingCompleted = completed)
        _stats.value = newStats
        persistStats(newStats)
    }

    private fun checkThresholdChange(oldState: BrainState, newState: BrainState) {
        if (oldState != newState && newState.isAlertOrHigher) {
            externalScope.launch {
                _thresholdCrossedEvents.emit(newState)
            }
        }
    }

    fun isAccessibilityServiceEnabled(): Boolean {
        if (BrainyAccessibilityService.isServiceConnected) return true

        return try {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
            val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            val expectedServiceName = "${context.packageName}/${BrainyAccessibilityService::class.java.canonicalName}"
            val expectedSimpleName = "${context.packageName}/${BrainyAccessibilityService::class.java.name}"

            enabledServices.any { service ->
                val id = service.id
                id.equals(expectedServiceName, ignoreCase = true) ||
                        id.equals(expectedSimpleName, ignoreCase = true) ||
                        id.contains("BrainyAccessibilityService")
            }
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        fun openUsageAccessSettings(context: Context) {
            try {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                try {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        }

        fun openAccessibilitySettings(context: Context) {
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                try {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        }
    }
}
