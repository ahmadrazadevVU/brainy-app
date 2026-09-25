package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BrainyBottomNavigation
import com.example.ui.components.BreakSessionModal
import com.example.ui.components.EmotionalReactionDialog
import com.example.ui.components.InteractiveTesterSheet
import com.example.ui.components.PlatformDetailSheet
import com.example.ui.components.TargetSetupSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.JourneyScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TargetSetupScreen
import com.example.ui.theme.BrainyAppTheme
import com.example.ui.viewmodel.BrainyViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    private val viewModel: BrainyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val stats by viewModel.stats.collectAsStateWithLifecycle()
            val brainState by viewModel.brainState.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val activePopup by viewModel.activePopupState.collectAsStateWithLifecycle()
            val isBreakOpen by viewModel.isBreakModalOpen.collectAsStateWithLifecycle()
            val isTargetSheetOpen by viewModel.isTargetSetupSheetOpen.collectAsStateWithLifecycle()
            val isDevTesterOpen by viewModel.isDevTesterOpen.collectAsStateWithLifecycle()
            val selectedPlatformDetail by viewModel.selectedPlatformDetail.collectAsStateWithLifecycle()

            // Local navigation state for onboarding flow
            var onboardingStep by remember { mutableStateOf(0) } // 0 = welcome, 1 = target setup

            BrainyAppTheme(themeMode = stats.themeMode) {
                if (!stats.isOnboardingCompleted) {
                    if (onboardingStep == 0) {
                        OnboardingScreen(
                            onGetStarted = { onboardingStep = 1 }
                        )
                    } else {
                        TargetSetupScreen(
                            initialTarget = stats.targetScrolls,
                            onTargetConfirmed = { chosenTarget ->
                                viewModel.setTarget(chosenTarget)
                                viewModel.completeOnboarding()
                                onboardingStep = 0
                            }
                        )
                    }
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            BrainyBottomNavigation(
                                currentTab = currentTab,
                                onTabSelected = { viewModel.selectTab(it) }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tab_transition"
                            ) { tab ->
                                when (tab) {
                                    NavigationTab.HOME -> HomeScreen(
                                        stats = stats,
                                        brainState = brainState,
                                        onTakeBreakClick = { viewModel.openBreakModal() },
                                        onEnableTrackingClick = { viewModel.openAccessibilitySettings() },
                                        onQuestClick = { viewModel.openBreakModal() },
                                        onProfileClick = { viewModel.selectTab(NavigationTab.PROFILE) },
                                        onDevTesterClick = { viewModel.toggleDevTester() },
                                        onPlatformClick = { viewModel.openPlatformDetail(it) }
                                    )

                                    NavigationTab.JOURNEY -> JourneyScreen(
                                        stats = stats
                                    )

                                    NavigationTab.INSIGHTS -> InsightsScreen(
                                        stats = stats
                                    )

                                    NavigationTab.PROFILE -> ProfileScreen(
                                        stats = stats,
                                        onOpenTargetSetup = { viewModel.openTargetSetup() },
                                        onChangeTheme = { viewModel.setThemeMode(it) },
                                        onResetData = { viewModel.resetDaily() },
                                        onRestartOnboarding = { viewModel.restartOnboarding() },
                                        onOpenUsageSettings = { viewModel.openUsageAccessSettings() },
                                        onOpenAccessibilitySettings = { viewModel.openAccessibilitySettings() }
                                    )
                                }
                            }
                        }
                    }

                    // Modals and Bottom Sheets
                    if (activePopup != null) {
                        EmotionalReactionDialog(
                            brainState = activePopup!!,
                            onTakeBreak = { viewModel.openBreakModal() },
                            onDismiss = { viewModel.dismissPopup() }
                        )
                    }

                    if (isBreakOpen) {
                        BreakSessionModal(
                            onComplete = { minutes -> viewModel.completeBreakSession(minutes) },
                            onClose = { viewModel.closeBreakModal() }
                        )
                    }

                    if (isTargetSheetOpen) {
                        TargetSetupSheet(
                            currentTarget = stats.targetScrolls,
                            onSaveTarget = { viewModel.setTarget(it) },
                            onDismiss = { viewModel.closeTargetSetup() }
                        )
                    }

                    if (isDevTesterOpen) {
                        InteractiveTesterSheet(
                            todayScrolls = stats.todayScrolls,
                            target = stats.targetScrolls,
                            onAddScrolls = { viewModel.addScrolls(it) },
                            onSetScrolls = { viewModel.setScrollsDirectly(it) },
                            onReset = { viewModel.setScrollsDirectly(0) },
                            onDismiss = { viewModel.toggleDevTester() }
                        )
                    }

                    selectedPlatformDetail?.let { detail ->
                        PlatformDetailSheet(
                            detail = detail,
                            target = stats.targetScrolls,
                            onDismiss = { viewModel.closePlatformDetail() }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshDeviceMetrics()
    }
}
