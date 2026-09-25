package com.example.data.service

import com.example.data.model.BrainState
import com.example.data.model.ThemeMode
import com.example.data.model.WellbeingStats
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Clean architectural abstraction for digital wellbeing & scroll tracking.
 * Phase 1 uses MockTrackingService with persistent local storage.
 * Phase 2 will bind native Android Accessibility / UsageStats tracking.
 */
interface TrackingService {
    val stats: StateFlow<WellbeingStats>
    val thresholdCrossedEvents: SharedFlow<BrainState>

    fun setTarget(target: Int)
    fun recordScrollDelta(delta: Int)
    fun setScrollsDirectly(count: Int)
    fun resetDemoSimulation()
    fun completeBreakSession(minutes: Int = 5)
    fun resetDaily()
    fun setThemeMode(mode: ThemeMode)
    fun setOnboardingCompleted(completed: Boolean)
}
