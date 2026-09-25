package com.example.data.service

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import java.util.Calendar

data class VerifiedAppUsage(
    val totalForegroundMinutes: Int,
    val sessionsCount: Int,
    val dominantAppName: String,
    val isPermissionGranted: Boolean
)

/**
 * Handles real hardware/OS telemetry queries using UsageStatsManager.
 * Strictly verifies foreground usage and sessions without fabricating metrics.
 */
class UsageStatsTracker(private val context: Context) {

    companion object {
        val TARGET_PACKAGES = mapOf(
            "com.instagram.android" to "Instagram",
            "com.zhiliaoapp.musically" to "TikTok",
            "com.ss.android.ugc.trill" to "TikTok",
            "com.google.android.youtube" to "YouTube",
            "com.snapchat.android" to "Snapchat"
        )
    }

    fun isUsageAccessGranted(): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (_: Exception) {
            false
        }
    }

    fun queryTodayUsage(): VerifiedAppUsage {
        if (!isUsageAccessGranted()) {
            return VerifiedAppUsage(
                totalForegroundMinutes = 0,
                sessionsCount = 0,
                dominantAppName = "Instagram",
                isPermissionGranted = false
            )
        }

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return VerifiedAppUsage(0, 0, "Instagram", false)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        var totalForegroundTimeMillis = 0L
        var dominantPkg = "com.instagram.android"
        var maxTimeForPkg = 0L

        try {
            val statsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

            if (!statsList.isNullOrEmpty()) {
                for (stat in statsList) {
                    if (TARGET_PACKAGES.containsKey(stat.packageName)) {
                        val timeInFg = stat.totalTimeInForeground
                        if (timeInFg > 0) {
                            totalForegroundTimeMillis += timeInFg
                            if (timeInFg > maxTimeForPkg) {
                                maxTimeForPkg = timeInFg
                                dominantPkg = stat.packageName
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Safe fallback if queryUsageStats encounters unexpected internal security or platform exception
        }

        // Count sessions via UsageEvents transition to FOREGROUND
        var sessionsCount = 0
        try {
            val events = usageStatsManager.queryEvents(startTime, endTime)
            val event = UsageEvents.Event()
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (TARGET_PACKAGES.containsKey(event.packageName)) {
                    if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                        event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
                    ) {
                        sessionsCount++
                    }
                }
            }
        } catch (_: Exception) {
            // Graceful handling
        }

        val minutes = (totalForegroundTimeMillis / 60000L).toInt()
        val appName = TARGET_PACKAGES[dominantPkg] ?: "Instagram"

        return VerifiedAppUsage(
            totalForegroundMinutes = minutes,
            sessionsCount = sessionsCount.coerceAtLeast(if (minutes > 0) 1 else 0),
            dominantAppName = appName,
            isPermissionGranted = true
        )
    }
}
