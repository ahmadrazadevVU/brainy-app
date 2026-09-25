package com.example.data.service

import android.content.Context
import android.content.SharedPreferences
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

/**
 * Concrete implementation of TrackingService for Phase 1 presentation.
 * Uses realistic mock values matching approved UI, persists values locally,
 * and handles threshold crossing detection for emotional reactions.
 */
class MockTrackingService(
    context: Context,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : TrackingService {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("brainy_wellbeing_prefs", Context.MODE_PRIVATE)

    private val _stats = MutableStateFlow(loadPersistedStats())
    override val stats: StateFlow<WellbeingStats> = _stats.asStateFlow()

    private val _thresholdCrossedEvents = MutableSharedFlow<BrainState>(extraBufferCapacity = 1)
    override val thresholdCrossedEvents: SharedFlow<BrainState> = _thresholdCrossedEvents.asSharedFlow()

    private fun loadPersistedStats(): WellbeingStats {
        val target = prefs.getInt("target_scrolls", 100)
        val ig = prefs.getInt("platform_ig", 0)
        val tt = prefs.getInt("platform_tt", 0)
        val yt = prefs.getInt("platform_yt", 0)
        val sc = prefs.getInt("platform_sc", 0)
        val platformStats = PlatformStats(ig, tt, yt, sc)

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

        return WellbeingStats(
            targetScrolls = target,
            platformStats = platformStats,
            todayScrolls = platformStats.totalScrolls,
            screenTimeMinutes = screenTime,
            sessionsCount = sessions,
            currentXp = xp,
            level = level,
            currentLevelXp = levelXp,
            levelTargetXp = levelTargetXp,
            breakQuestCompleted = breakQuest,
            themeMode = themeMode,
            isOnboardingCompleted = onboardingDone
        )
    }

    private fun persistStats(stats: WellbeingStats) {
        prefs.edit()
            .putInt("target_scrolls", stats.targetScrolls)
            .putInt("platform_ig", stats.platformStats.instagram)
            .putInt("platform_tt", stats.platformStats.tiktok)
            .putInt("platform_yt", stats.platformStats.youtube)
            .putInt("platform_sc", stats.platformStats.snapchat)
            .putInt("today_scrolls", stats.todayScrolls)
            .putInt("screen_time_mins", stats.screenTimeMinutes)
            .putInt("sessions_count", stats.sessionsCount)
            .putInt("current_xp", stats.currentXp)
            .putInt("level", stats.level)
            .putInt("level_xp", stats.currentLevelXp)
            .putInt("level_target_xp", stats.levelTargetXp)
            .putInt("break_quest_completed", stats.breakQuestCompleted)
            .putInt("theme_mode", stats.themeMode.ordinal)
            .putBoolean("onboarding_completed", stats.isOnboardingCompleted)
            .apply()
    }

    override fun setTarget(target: Int) {
        val oldState = _stats.value.brainState
        val newStats = _stats.value.copy(targetScrolls = target)
        _stats.value = newStats
        persistStats(newStats)
        checkThresholdChange(oldState, newStats.brainState)
    }

    override fun recordScrollDelta(delta: Int) {
        val oldState = _stats.value.brainState
        val currentScrolls = _stats.value.todayScrolls
        val newScrolls = (currentScrolls + delta).coerceAtLeast(0)
        val addedMins = if (delta > 0) (delta / 15).coerceAtLeast(1) else 0
        val newStats = _stats.value.copy(
            todayScrolls = newScrolls,
            screenTimeMinutes = _stats.value.screenTimeMinutes + addedMins
        )
        _stats.value = newStats
        persistStats(newStats)
        checkThresholdChange(oldState, newStats.brainState)
    }

    override fun setScrollsDirectly(count: Int) {
        val oldState = _stats.value.brainState
        val safeCount = count.coerceAtLeast(0)
        val newStats = _stats.value.copy(todayScrolls = safeCount)
        _stats.value = newStats
        persistStats(newStats)
        checkThresholdChange(oldState, newStats.brainState)
    }

    override fun resetDemoSimulation() {
        val oldState = _stats.value.brainState
        val newStats = _stats.value.copy(todayScrolls = 0)
        _stats.value = newStats
        persistStats(newStats)
        checkThresholdChange(oldState, newStats.brainState)
    }

    override fun completeBreakSession(minutes: Int) {
        val current = _stats.value
        val xpGain = 100
        val newTotalXp = current.currentXp + xpGain
        val newLevelXp = current.currentLevelXp + xpGain
        val newQuestCompleted = (current.breakQuestCompleted + 1).coerceAtMost(current.breakQuestTotal)

        // Check level up
        val (finalLevel, finalLevelXp) = if (newLevelXp >= current.levelTargetXp) {
            Pair(current.level + 1, newLevelXp - current.levelTargetXp)
        } else {
            Pair(current.level, newLevelXp)
        }

        // Cool down scrolls slightly to represent brain recovery
        val relaxedScrolls = (current.todayScrolls - 15).coerceAtLeast(0)

        val newStats = current.copy(
            currentXp = newTotalXp,
            level = finalLevel,
            currentLevelXp = finalLevelXp,
            breakQuestCompleted = newQuestCompleted,
            todayScrolls = relaxedScrolls
        )
        _stats.value = newStats
        persistStats(newStats)

        // Emit celebration state
        externalScope.launch {
            _thresholdCrossedEvents.emit(BrainState.CELEBRATING)
        }
    }

    override fun resetDaily() {
        val current = _stats.value
        val newStats = current.copy(
            todayScrolls = 0,
            screenTimeMinutes = 0,
            sessionsCount = 1
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
}
