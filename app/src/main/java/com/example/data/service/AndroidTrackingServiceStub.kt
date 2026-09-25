package com.example.data.service

import com.example.data.model.BrainState
import com.example.data.model.ThemeMode
import com.example.data.model.WellbeingStats
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ARCHITECTURE SPECIFICATION FOR PHASE 2:
 *
 * FutureAndroidTrackingService will replace MockTrackingService when native
 * device permissions and telemetry tracking are integrated.
 *
 * How Phase 2 integrates:
 * 1. AccessibilityService (Short-video scroll detection):
 *    - Registers for AccessibilityEvent.TYPE_VIEW_SCROLLED
 *    - Filters by target short-video package names:
 *      * com.instagram.android (Instagram Reels)
 *      * com.zhiliaoapp.musically / com.ss.android.ugc.trill (TikTok)
 *      * com.google.android.youtube (YouTube Shorts)
 *    - On scroll event detection, emits increment through `recordScrollDelta(1)`.
 *
 * 2. UsageStatsManager (App Screen Time & Session frequency):
 *    - Queries UsageStatsManager.queryUsageStats() within 24-hour interval
 *    - Calculates active foreground time in short-video apps
 *    - Counts distinct foreground transition sessions
 *
 * 3. Plug-in mechanism:
 *    - In the ViewModel or DI container, swap `MockTrackingService` with
 *      `FutureAndroidTrackingService`. The presentation layer and UI will
 *      require 0 changes because they consume only the `TrackingService` interface.
 */
class AndroidTrackingServiceStub : TrackingService {
    override val stats: StateFlow<WellbeingStats> =
        MutableStateFlow(WellbeingStats())

    override val thresholdCrossedEvents: SharedFlow<BrainState> =
        MutableSharedFlow()

    override fun setTarget(target: Int) {
        // Phase 2 implementation: persist to Room / DataStore & update Accessibility thresholds
    }

    override fun recordScrollDelta(delta: Int) {
        // Phase 2 implementation: receive from AccessibilityService onScroll
    }

    override fun setScrollsDirectly(count: Int) {
        // Phase 2 implementation: synchronize with background service
    }

    override fun resetDemoSimulation() {
        // Phase 2 implementation: exit demo mode
    }

    override fun completeBreakSession(minutes: Int) {
        // Phase 2 implementation: award XP and update break quest
    }

    override fun resetDaily() {
        // Phase 2 implementation: reset midnight usage metrics
    }

    override fun setThemeMode(mode: ThemeMode) {
        // Phase 2 implementation: persist user theme
    }

    override fun setOnboardingCompleted(completed: Boolean) {
        // Phase 2 implementation: persist onboarding state
    }
}
