package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

/**
 * The emotional states of Brainy, calculated dynamically relative to the user's daily target.
 * Never hardcoded to 100 — scales smoothly with any user-chosen target.
 */
enum class BrainState(
    val title: String,
    val headline: String,
    val message: String,
    val recommendationTitle: String,
    val recommendationSubtitle: String,
    @DrawableRes val imageRes: Int,
    val isAlertOrHigher: Boolean = false,
    val showOrbitingStars: Boolean = false
) {
    HAPPY(
        title = "Happy & Sharp",
        headline = "You're doing great.",
        message = "Mindful scrolling. Brain is feeling fresh and energized!",
        recommendationTitle = "Brain is energized",
        recommendationSubtitle = "Keep up this healthy balance today.",
        imageRes = R.drawable.brainy_mascot_happy,
        isAlertOrHigher = false,
        showOrbitingStars = false
    ),
    CALM(
        title = "Calm & Smooth",
        headline = "Smooth sailing.",
        message = "Gentle pace. You are well within your healthy zone.",
        recommendationTitle = "Pacing looks excellent",
        recommendationSubtitle = "Enjoy your content mindfully.",
        imageRes = R.drawable.brainy_mascot_happy,
        isAlertOrHigher = false,
        showOrbitingStars = false
    ),
    ALERT(
        title = "Noticing the Scrolls",
        headline = "Your brain noticed that.",
        message = "Halfway through your daily target. Brain feels a gentle stir.",
        recommendationTitle = "Brain noticed the momentum",
        recommendationSubtitle = "Consider pacing your next scroll session.",
        imageRes = R.drawable.brainy_mascot_dizzy,
        isAlertOrHigher = true,
        showOrbitingStars = true
    ),
    NERVOUS(
        title = "Almost There",
        headline = "Almost at your target...",
        message = "Brain is starting to spin slightly. Stars are drifting in.",
        recommendationTitle = "Brain feeling a little dizzy",
        recommendationSubtitle = "Maybe give it a 5 minute reset soon.",
        imageRes = R.drawable.brainy_mascot_dizzy,
        isAlertOrHigher = true,
        showOrbitingStars = true
    ),
    TARGET_REACHED(
        title = "Target Reached",
        headline = "Target reached.",
        message = "You hit your daily limit. Great moment to pause and stretch!",
        recommendationTitle = "Daily target achieved",
        recommendationSubtitle = "Time to rest your eyes and claim your +100 XP.",
        imageRes = R.drawable.brainy_mascot_celebrating,
        isAlertOrHigher = true,
        showOrbitingStars = true
    ),
    TIRED(
        title = "Mild Reaction",
        headline = "Umm… we passed your target.",
        message = "Brain is rubbing its eyes. Short-video momentum is climbing.",
        recommendationTitle = "Brain is getting tired",
        recommendationSubtitle = "A quick water break would work wonders.",
        imageRes = R.drawable.brainy_mascot_dizzy,
        isAlertOrHigher = true,
        showOrbitingStars = true
    ),
    OVERLOADED(
        title = "Overloaded",
        headline = "Another 20?",
        message = "Sensory overload detected! Dopamine receptors need a breather.",
        recommendationTitle = "Sensory overload warning",
        recommendationSubtitle = "Step away from the feed for 5 minutes.",
        imageRes = R.drawable.brainy_mascot_overloaded,
        isAlertOrHigher = true,
        showOrbitingStars = true
    ),
    STRONG_OVERLOAD(
        title = "Strong Overload",
        headline = "I noticed that.",
        message = "Brain is comically spinning! The short videos have taken over.",
        recommendationTitle = "Comical overload state",
        recommendationSubtitle = "Brain is asking for mercy. Tap to reset!",
        imageRes = R.drawable.brainy_mascot_overloaded,
        isAlertOrHigher = true,
        showOrbitingStars = true
    ),
    BRAIN_BREAK(
        title = "Emergency Brain Break",
        headline = "Brain break?",
        message = "Emergency cool-down activated! Time for real-world balance.",
        recommendationTitle = "Emergency Brain Break",
        recommendationSubtitle = "Let's do a 5-minute breathing reset together.",
        imageRes = R.drawable.brainy_mascot_overloaded,
        isAlertOrHigher = true,
        showOrbitingStars = true
    ),
    CELEBRATING(
        title = "Celebrating Reset",
        headline = "Brain restored!",
        message = "Fresh mind, rejuvenated focus, and +100 XP gained!",
        recommendationTitle = "Wellbeing reset complete",
        recommendationSubtitle = "Brain is happy and recharged.",
        imageRes = R.drawable.brainy_mascot_celebrating,
        isAlertOrHigher = false,
        showOrbitingStars = false
    );

    companion object {
        /**
         * Dynamically compute the brain emotional state relative to user's target.
         * Scales with any user-chosen target (e.g., 50, 100, 150, 200).
         */
        fun fromScrolls(scrolls: Int, target: Int): BrainState {
            val safeTarget = target.coerceAtLeast(20)
            return when {
                scrolls >= safeTarget + 100 -> BRAIN_BREAK
                scrolls >= safeTarget + 60 -> STRONG_OVERLOAD
                scrolls >= safeTarget + 40 -> OVERLOADED
                scrolls >= safeTarget + 20 -> TIRED
                scrolls >= safeTarget -> TARGET_REACHED
                scrolls >= (safeTarget * 0.8).toInt() -> NERVOUS
                scrolls >= (safeTarget * 0.5).toInt() -> ALERT
                scrolls >= (safeTarget * 0.25).toInt() -> CALM
                scrolls > 0 -> HAPPY
                else -> CALM
            }
        }
    }
}
