package com.example.data.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * Investigates and captures verified scroll events in supported short-video apps.
 *
 * Implements strict debouncing and duplicate filtering:
 * 1. Package filtering (Instagram, TikTok, YouTube Shorts).
 * 2. Minimum cooldown window (450ms) to ensure 1 physical user swipe/fling is counted
 *    as exactly 1 verified scroll event instead of multiple micro-scroll events.
 * 3. Safe null-handling and lifecycle management.
 */
class BrainyAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "BrainyAccessibility"
        private const val DEBOUNCE_THRESHOLD_MS = 450L

        private val SUPPORTED_PACKAGES = setOf(
            "com.instagram.android",
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill",
            "com.google.android.youtube",
            "com.snapchat.android"
        )

        @Volatile
        var isServiceConnected: Boolean = false
            private set

        private val listeners = mutableListOf<ScrollEventListener>()

        fun addListener(listener: ScrollEventListener) {
            synchronized(listeners) {
                if (!listeners.contains(listener)) {
                    listeners.add(listener)
                }
            }
        }

        fun removeListener(listener: ScrollEventListener) {
            synchronized(listeners) {
                listeners.remove(listener)
            }
        }

        internal fun notifyScrollDetected(packageName: String, eventTime: Long) {
            synchronized(listeners) {
                for (listener in listeners) {
                    try {
                        listener.onScrollDetected(packageName, eventTime)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error notifying scroll listener", e)
                    }
                }
            }
        }
    }

    interface ScrollEventListener {
        fun onScrollDetected(packageName: String, eventTime: Long)
        fun onServiceStatusChanged(isConnected: Boolean)
    }

    private var lastScrollTimestamp = 0L
    private var lastPackageName: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        isServiceConnected = true
        Log.d(TAG, "BrainyAccessibilityService connected and observing verified scrolls")
        notifyStatusChanged(true)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val pkgName = event.packageName?.toString() ?: return
        if (!SUPPORTED_PACKAGES.contains(pkgName)) return

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            // Requirement 9: Do not count arbitrary YouTube scrolling/navigation as a Short
            if (pkgName == "com.google.android.youtube" && !isLikelyYouTubeShortsEvent(event)) {
                Log.d(TAG, "YouTube feed/navigation scroll filtered out (not Shorts carousel)")
                return
            }

            val currentTime = System.currentTimeMillis()

            // Debounce check: Prevent duplicate micro-scroll events from single swipe gestures
            if (currentTime - lastScrollTimestamp >= DEBOUNCE_THRESHOLD_MS) {
                lastScrollTimestamp = currentTime
                lastPackageName = pkgName

                val appLabel = UsageStatsTracker.TARGET_PACKAGES[pkgName] ?: "Short Video"
                Log.d(TAG, "Verified short-video scroll detected in $appLabel ($pkgName)")

                notifyScrollDetected(pkgName, currentTime)
            }
        }
    }

    private fun isLikelyYouTubeShortsEvent(event: AccessibilityEvent): Boolean {
        val className = event.className?.toString() ?: ""
        if (className.contains("ViewPager", ignoreCase = true) || className.contains("Reel", ignoreCase = true)) {
            return true
        }

        val desc = event.contentDescription?.toString() ?: ""
        if (desc.contains("Shorts", ignoreCase = true) || desc.contains("Reel", ignoreCase = true)) {
            return true
        }

        val source = try { event.source } catch (_: Exception) { null }
        if (source != null) {
            try {
                val resName = source.viewIdResourceName ?: ""
                if (resName.contains("reel", ignoreCase = true) || resName.contains("short", ignoreCase = true)) {
                    return true
                }
                var parent = source.parent
                var depth = 0
                while (parent != null && depth < 4) {
                    val parentRes = parent.viewIdResourceName ?: ""
                    if (parentRes.contains("reel", ignoreCase = true) || parentRes.contains("short", ignoreCase = true)) {
                        return true
                    }
                    parent = parent.parent
                    depth++
                }
            } catch (_: Exception) {
            }
        }

        return false
    }

    override fun onInterrupt() {
        Log.w(TAG, "BrainyAccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceConnected = false
        Log.d(TAG, "BrainyAccessibilityService destroyed")
        notifyStatusChanged(false)
    }

    private fun notifyStatusChanged(connected: Boolean) {
        synchronized(listeners) {
            for (listener in listeners) {
                try {
                    listener.onServiceStatusChanged(connected)
                } catch (e: Exception) {
                    Log.e(TAG, "Error notifying service status change", e)
                }
            }
        }
    }
}
