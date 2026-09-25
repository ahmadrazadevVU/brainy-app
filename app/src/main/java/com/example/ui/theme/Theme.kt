package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeMode

@Immutable
data class BrainyExtendedColors(
    val background: Color,
    val surface: Color,
    val surfaceSubtle: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val brandIndigo: Color,
    val recCardBg: Color,
    val recCardBorder: Color,
    val statPinkBg: Color,
    val statPinkBorder: Color,
    val statPinkIcon: Color,
    val statBlueBg: Color,
    val statBlueBorder: Color,
    val statBlueIcon: Color,
    val statCyanBg: Color,
    val statCyanBorder: Color,
    val statCyanIcon: Color,
    val statAmberBg: Color,
    val statAmberBorder: Color,
    val statAmberIcon: Color,
    val badgeGoldBg: Color,
    val badgeGoldText: Color,
    val isDark: Boolean
)

val LightExtendedColors = BrainyExtendedColors(
    background = LightBackground,
    surface = LightSurface,
    surfaceSubtle = LightSurfaceSubtle,
    border = LightBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,
    brandIndigo = BrainyIndigo,
    recCardBg = LightRecCardBg,
    recCardBorder = LightRecCardBorder,
    statPinkBg = LightStatPinkBg,
    statPinkBorder = LightStatPinkBorder,
    statPinkIcon = LightStatPinkIcon,
    statBlueBg = LightStatBlueBg,
    statBlueBorder = LightStatBlueBorder,
    statBlueIcon = LightStatBlueIcon,
    statCyanBg = LightStatCyanBg,
    statCyanBorder = LightStatCyanBorder,
    statCyanIcon = LightStatCyanIcon,
    statAmberBg = LightStatAmberBg,
    statAmberBorder = LightStatAmberBorder,
    statAmberIcon = LightStatAmberIcon,
    badgeGoldBg = BadgeGoldBgLight,
    badgeGoldText = BadgeGoldTextLight,
    isDark = false
)

val DarkExtendedColors = BrainyExtendedColors(
    background = DarkBackground,
    surface = DarkSurface,
    surfaceSubtle = DarkSurfaceSubtle,
    border = DarkBorder,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    brandIndigo = BrainyIndigoLight,
    recCardBg = DarkRecCardBg,
    recCardBorder = DarkRecCardBorder,
    statPinkBg = DarkStatPinkBg,
    statPinkBorder = DarkStatPinkBorder,
    statPinkIcon = DarkStatPinkIcon,
    statBlueBg = DarkStatBlueBg,
    statBlueBorder = DarkStatBlueBorder,
    statBlueIcon = DarkStatBlueIcon,
    statCyanBg = DarkStatCyanBg,
    statCyanBorder = DarkStatCyanBorder,
    statCyanIcon = DarkStatCyanIcon,
    statAmberBg = DarkStatAmberBg,
    statAmberBorder = DarkStatAmberBorder,
    statAmberIcon = DarkStatAmberIcon,
    badgeGoldBg = BadgeGoldBgDark,
    badgeGoldText = BadgeGoldTextDark,
    isDark = true
)

val LocalBrainyColors = staticCompositionLocalOf { LightExtendedColors }

object BrainyTheme {
    val colors: BrainyExtendedColors
        @Composable
        get() = LocalBrainyColors.current
}

private val MaterialDarkColors = darkColorScheme(
    primary = BrainyIndigoLight,
    onPrimary = Color.White,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val MaterialLightColors = lightColorScheme(
    primary = BrainyIndigo,
    onPrimary = Color.White,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

@Composable
fun BrainyAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val extendedColors = if (isDark) DarkExtendedColors else LightExtendedColors
    val materialColors = if (isDark) MaterialDarkColors else MaterialLightColors

    CompositionLocalProvider(LocalBrainyColors provides extendedColors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = Typography,
            content = content
        )
    }
}
