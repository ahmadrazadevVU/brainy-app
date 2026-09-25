package com.example.data.model

enum class ShortVideoPlatform(
    val id: String,
    val displayName: String,
    val shortName: String,
    val packageName: String,
    val markText: String,
    val accentHex: Long
) {
    INSTAGRAM(
        id = "instagram",
        displayName = "Instagram Reels",
        shortName = "Instagram",
        packageName = "com.instagram.android",
        markText = "IG",
        accentHex = 0xFFE1306C
    ),
    TIKTOK(
        id = "tiktok",
        displayName = "TikTok",
        shortName = "TikTok",
        packageName = "com.zhiliaoapp.musically",
        markText = "TT",
        accentHex = 0xFF00F2FE
    ),
    YOUTUBE(
        id = "youtube",
        displayName = "YouTube Shorts",
        shortName = "YouTube",
        packageName = "com.google.android.youtube",
        markText = "YT",
        accentHex = 0xFFFF0000
    ),
    SNAPCHAT(
        id = "snapchat",
        displayName = "Snapchat",
        shortName = "Snapchat",
        packageName = "com.snapchat.android",
        markText = "SC",
        accentHex = 0xFFEAB308
    )
}

data class PlatformDetail(
    val platform: ShortVideoPlatform,
    val detectedScrolls: Int,
    val usageMinutes: Int,
    val sessionsCount: Int,
    val brainComment: String
)

/**
 * Data architecture supporting separate tracked scrolls for each short-video platform.
 * Guarantee: totalScrolls always equals instagram + tiktok + youtube + snapchat.
 */
data class PlatformStats(
    val instagram: Int = 0,
    val tiktok: Int = 0,
    val youtube: Int = 0,
    val snapchat: Int = 0,
    val instagramMinutes: Int = 0,
    val instagramSessions: Int = 0,
    val tiktokMinutes: Int = 0,
    val tiktokSessions: Int = 0,
    val youtubeMinutes: Int = 0,
    val youtubeSessions: Int = 0,
    val snapchatMinutes: Int = 0,
    val snapchatSessions: Int = 0
) {
    val totalScrolls: Int
        get() = instagram + tiktok + youtube + snapchat

    fun getDetail(platform: ShortVideoPlatform, target: Int): PlatformDetail {
        return when (platform) {
            ShortVideoPlatform.INSTAGRAM -> PlatformDetail(
                platform = platform,
                detectedScrolls = instagram,
                usageMinutes = instagramMinutes,
                sessionsCount = instagramSessions,
                brainComment = when {
                    instagram == 0 -> "Ready to track when you browse Reels. Mindful pacing is your superpower!"
                    instagram > target * 0.3 -> "Reels are tempting today! A 5-minute pause keeps your mind fresh."
                    else -> "Mindful pace on Reels. You're staying in control."
                }
            )
            ShortVideoPlatform.TIKTOK -> PlatformDetail(
                platform = platform,
                detectedScrolls = tiktok,
                usageMinutes = tiktokMinutes,
                sessionsCount = tiktokSessions,
                brainComment = when {
                    tiktok == 0 -> "For You feed tracking is ready. Starting fresh today!"
                    tiktok > 20 -> "The algorithm is moving fast. Brain noticed the momentum!"
                    else -> "Solid discipline on the For You feed."
                }
            )
            ShortVideoPlatform.YOUTUBE -> PlatformDetail(
                platform = platform,
                detectedScrolls = youtube,
                usageMinutes = youtubeMinutes,
                sessionsCount = youtubeSessions,
                brainComment = when {
                    youtube == 0 -> "Shorts detection is active. Standard YouTube video feeds are filtered out."
                    youtube > 15 -> "Shorts can sneak up on you. Time for a quick screen rest."
                    else -> "Light Shorts viewing. Looking sharp!"
                }
            )
            ShortVideoPlatform.SNAPCHAT -> PlatformDetail(
                platform = platform,
                detectedScrolls = snapchat,
                usageMinutes = snapchatMinutes,
                sessionsCount = snapchatSessions,
                brainComment = when {
                    snapchat == 0 -> "Spotlight tracking is ready. Looking calm and balanced."
                    snapchat > 10 -> "Spotlight stories checked. Time to step into the real world!"
                    else -> "Just a quick check-in. Pacing is smooth."
                }
            )
        }
    }
}
