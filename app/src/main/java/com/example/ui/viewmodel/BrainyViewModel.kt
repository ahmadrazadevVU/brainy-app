package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BrainState
import com.example.data.model.ThemeMode
import com.example.data.model.WellbeingStats
import com.example.data.service.AndroidTrackingService
import com.example.data.service.TrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab {
    HOME,
    JOURNEY,
    INSIGHTS,
    PROFILE
}

class BrainyViewModel @JvmOverloads constructor(
    application: Application,
    private val trackingService: TrackingService = AndroidTrackingService(application)
) : AndroidViewModel(application) {

    val stats: StateFlow<WellbeingStats> = trackingService.stats

    val brainState: StateFlow<BrainState> = stats
        .map { it.brainState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = stats.value.brainState
        )

    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _activePopupState = MutableStateFlow<BrainState?>(null)
    val activePopupState: StateFlow<BrainState?> = _activePopupState.asStateFlow()

    private val _isBreakModalOpen = MutableStateFlow(false)
    val isBreakModalOpen: StateFlow<Boolean> = _isBreakModalOpen.asStateFlow()

    private val _isTargetSetupSheetOpen = MutableStateFlow(false)
    val isTargetSetupSheetOpen: StateFlow<Boolean> = _isTargetSetupSheetOpen.asStateFlow()

    private val _isDevTesterOpen = MutableStateFlow(false)
    val isDevTesterOpen: StateFlow<Boolean> = _isDevTesterOpen.asStateFlow()

    private val _selectedPlatformDetail = MutableStateFlow<com.example.data.model.PlatformDetail?>(null)
    val selectedPlatformDetail: StateFlow<com.example.data.model.PlatformDetail?> = _selectedPlatformDetail.asStateFlow()

    init {
        viewModelScope.launch {
            trackingService.thresholdCrossedEvents.collect { thresholdState ->
                if (thresholdState != BrainState.CELEBRATING) {
                    _activePopupState.value = thresholdState
                }
            }
        }
    }

    fun refreshDeviceMetrics() {
        if (trackingService is AndroidTrackingService) {
            trackingService.refreshDeviceMetrics()
        }
    }

    fun openUsageAccessSettings() {
        AndroidTrackingService.openUsageAccessSettings(getApplication())
    }

    fun openAccessibilitySettings() {
        AndroidTrackingService.openAccessibilitySettings(getApplication())
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun dismissPopup() {
        _activePopupState.value = null
    }

    fun openBreakModal() {
        _activePopupState.value = null
        _isBreakModalOpen.value = true
    }

    fun closeBreakModal() {
        _isBreakModalOpen.value = false
    }

    fun completeBreakSession(minutes: Int = 5) {
        trackingService.completeBreakSession(minutes)
        _isBreakModalOpen.value = false
    }

    fun setTarget(target: Int) {
        trackingService.setTarget(target)
        _isTargetSetupSheetOpen.value = false
    }

    fun openTargetSetup() {
        _isTargetSetupSheetOpen.value = true
    }

    fun closeTargetSetup() {
        _isTargetSetupSheetOpen.value = false
    }

    fun toggleDevTester() {
        _isDevTesterOpen.value = !_isDevTesterOpen.value
    }

    fun addScrolls(delta: Int) {
        trackingService.recordScrollDelta(delta)
    }

    fun setScrollsDirectly(count: Int) {
        trackingService.setScrollsDirectly(count)
    }

    fun resetDemoSimulation() {
        trackingService.resetDemoSimulation()
    }

    fun resetDaily() {
        trackingService.resetDaily()
    }

    fun setThemeMode(mode: ThemeMode) {
        trackingService.setThemeMode(mode)
    }

    fun completeOnboarding() {
        trackingService.setOnboardingCompleted(true)
    }

    fun restartOnboarding() {
        trackingService.setOnboardingCompleted(false)
    }

    fun openPlatformDetail(platform: com.example.data.model.ShortVideoPlatform) {
        val currentStats = stats.value
        _selectedPlatformDetail.value = currentStats.platformStats.getDetail(platform, currentStats.targetScrolls)
    }

    fun closePlatformDetail() {
        _selectedPlatformDetail.value = null
    }
}
