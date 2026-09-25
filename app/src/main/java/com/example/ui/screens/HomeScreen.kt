package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.BrainState
import com.example.data.model.ShortVideoPlatform
import com.example.data.model.WellbeingStats
import com.example.ui.components.BrainMascotHero
import com.example.ui.components.BrainyTopBar
import com.example.ui.components.RecommendationCard
import com.example.ui.components.TodayStatsGrid
import com.example.ui.components.YourProgressCards
import com.example.ui.components.YourScrollsSection
import com.example.ui.theme.BrainyTheme

@Composable
fun HomeScreen(
    stats: WellbeingStats,
    brainState: BrainState,
    onTakeBreakClick: () -> Unit,
    onQuestClick: () -> Unit,
    onProfileClick: () -> Unit,
    onDevTesterClick: () -> Unit,
    onPlatformClick: (ShortVideoPlatform) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = BrainyTheme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("home_screen")
    ) {
        // Top Header
        BrainyTopBar(
            onProfileClick = onProfileClick,
            onDevTesterClick = onDevTesterClick
        )

        // Scrollable Main Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // Main Hero: Mascot + Real-time Emotional Reaction + Today's Scrolls
            BrainMascotHero(
                brainState = brainState,
                todayScrolls = stats.todayScrolls,
                targetScrolls = stats.targetScrolls
            )

            // Recommendation Card (e.g. "Brain feeling a little dizzy", Take a Break CTA)
            RecommendationCard(
                brainState = brainState,
                onTakeBreakClick = onTakeBreakClick
            )

            // "Your Scrolls" Multi-Platform Breakdown (Instagram, TikTok, YouTube, Snapchat)
            YourScrollsSection(
                platformStats = stats.platformStats,
                targetScrolls = stats.targetScrolls,
                onPlatformClick = onPlatformClick
            )

            // Today's Stats 4-card row
            TodayStatsGrid(
                stats = stats
            )

            // Your Progress (Level 07 and Daily Quest)
            YourProgressCards(
                stats = stats,
                onQuestClick = onQuestClick
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
