package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BrainState
import com.example.data.model.PlatformStats
import com.example.data.model.ShortVideoPlatform
import com.example.data.model.WellbeingStats
import com.example.data.service.AndroidTrackingService
import com.example.data.service.BrainyAccessibilityService
import com.example.data.service.MockTrackingService
import com.example.data.service.UsageStatsTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Brainy", appName)
    }

    @Test
    fun `test dynamic brain state thresholds with custom target`() {
        val target = 100
        assertEquals(BrainState.HAPPY, BrainState.fromScrolls(20, target))
        assertEquals(BrainState.ALERT, BrainState.fromScrolls(58, target))
        assertEquals(BrainState.TARGET_REACHED, BrainState.fromScrolls(100, target))
        assertEquals(BrainState.TIRED, BrainState.fromScrolls(120, target))
        assertEquals(BrainState.OVERLOADED, BrainState.fromScrolls(140, target))
        assertEquals(BrainState.STRONG_OVERLOAD, BrainState.fromScrolls(160, target))
        assertEquals(BrainState.BRAIN_BREAK, BrainState.fromScrolls(200, target))

        // Dynamic scaling with custom target 150
        val target150 = 150
        assertEquals(BrainState.HAPPY, BrainState.fromScrolls(30, target150))
        assertEquals(BrainState.ALERT, BrainState.fromScrolls(80, target150))
        assertEquals(BrainState.TARGET_REACHED, BrainState.fromScrolls(150, target150))
        assertEquals(BrainState.TIRED, BrainState.fromScrolls(170, target150))
    }

    @Test
    fun `test mock tracking service persistence and scroll updates`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val service = MockTrackingService(context)

        service.setTarget(120)
        assertEquals(120, service.stats.value.targetScrolls)

        service.setScrollsDirectly(50)
        assertEquals(50, service.stats.value.todayScrolls)

        service.recordScrollDelta(10)
        assertEquals(60, service.stats.value.todayScrolls)
    }

    @Test
    fun `test android tracking service verified scroll events`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val service = AndroidTrackingService(context)

        val initialScrolls = service.stats.value.todayScrolls
        val initialVerified = service.stats.value.verifiedScrollEvents

        // Simulate verified scroll event from BrainyAccessibilityService
        service.onScrollDetected("com.instagram.android", System.currentTimeMillis())

        assertEquals(initialScrolls + 1, service.stats.value.todayScrolls)
        assertEquals(initialVerified + 1, service.stats.value.verifiedScrollEvents)
        assertEquals("Instagram", service.stats.value.activeAppName)
    }

    @Test
    fun `test usage stats tracker graceful permission check`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tracker = UsageStatsTracker(context)

        // Verifies permission check runs safely without throwing exceptions in JVM
        val isGranted = tracker.isUsageAccessGranted()

        val usage = tracker.queryTodayUsage()
        assertNotNull(usage)
        assertEquals(isGranted, usage.isPermissionGranted)
    }

    @Test
    fun `test telemetry distinction in WellbeingStats`() {
        val stats = WellbeingStats(
            todayScrolls = 70,
            verifiedScrollEvents = 25,
            screenTimeMinutes = 45,
            verifiedScreenTimeMinutes = 30
        )
        assertEquals(70, stats.todayScrolls)
        assertEquals(25, stats.verifiedScrollEvents)
        assertEquals(45, stats.screenTimeMinutes)
        assertEquals(30, stats.verifiedScreenTimeMinutes)
    }

    @Test
    fun `test ViewModelProvider creation of BrainyViewModel`() {
        val application = ApplicationProvider.getApplicationContext<android.app.Application>()
        val factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        val viewModel = factory.create(com.example.ui.viewmodel.BrainyViewModel::class.java)
        assertNotNull(viewModel)
    }

    @Test
    fun `test multi-platform stats total scroll invariant`() {
        val platformStats = PlatformStats(
            instagram = 34,
            tiktok = 22,
            youtube = 13,
            snapchat = 7
        )
        assertEquals(76, platformStats.totalScrolls)
        assertEquals(platformStats.instagram + platformStats.tiktok + platformStats.youtube + platformStats.snapchat, platformStats.totalScrolls)

        // Detail sheet models
        val igDetail = platformStats.getDetail(ShortVideoPlatform.INSTAGRAM, target = 100)
        assertEquals(34, igDetail.detectedScrolls)
        assertEquals(42, igDetail.usageMinutes)
        assertEquals(3, igDetail.sessionsCount)
        assertNotNull(igDetail.brainComment)

        val ttDetail = platformStats.getDetail(ShortVideoPlatform.TIKTOK, target = 100)
        assertEquals(22, ttDetail.detectedScrolls)
        assertEquals(24, ttDetail.usageMinutes)
        assertEquals(2, ttDetail.sessionsCount)

        val ytDetail = platformStats.getDetail(ShortVideoPlatform.YOUTUBE, target = 100)
        assertEquals(13, ytDetail.detectedScrolls)

        val scDetail = platformStats.getDetail(ShortVideoPlatform.SNAPCHAT, target = 100)
        assertEquals(7, scDetail.detectedScrolls)
    }

    @Test
    fun `test ViewModel platform detail selection`() {
        val application = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.viewmodel.BrainyViewModel(application)

        assertEquals(null, viewModel.selectedPlatformDetail.value)

        viewModel.openPlatformDetail(ShortVideoPlatform.INSTAGRAM)
        val selected = viewModel.selectedPlatformDetail.value
        assertNotNull(selected)
        assertEquals(ShortVideoPlatform.INSTAGRAM, selected?.platform)

        viewModel.closePlatformDetail()
        assertEquals(null, viewModel.selectedPlatformDetail.value)
    }
}
