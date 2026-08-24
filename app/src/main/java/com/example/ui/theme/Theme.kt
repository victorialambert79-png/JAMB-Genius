package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalBlue500,
    onPrimary = Slate950,
    primaryContainer = RoyalBlue800,
    onPrimaryContainer = RoyalBlue100,
    secondary = Emerald500,
    onSecondary = Slate950,
    secondaryContainer = Emerald900,
    onSecondaryContainer = Emerald100,
    tertiary = Amber500,
    onTertiary = Slate950,
    tertiaryContainer = Amber700,
    onTertiaryContainer = Amber100,
    error = Crimson500,
    onError = PureWhite,
    errorContainer = Crimson700,
    onErrorContainer = Crimson100,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    outlineVariant = Slate800
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue600,
    onPrimary = PureWhite,
    primaryContainer = RoyalBlue50,
    onPrimaryContainer = RoyalBlue800,
    secondary = Emerald600,
    onSecondary = PureWhite,
    secondaryContainer = Emerald50,
    onSecondaryContainer = Emerald900,
    tertiary = Amber600,
    onTertiary = PureWhite,
    tertiaryContainer = Amber50,
    onTertiaryContainer = Amber700,
    error = Crimson600,
    onError = PureWhite,
    errorContainer = Crimson50,
    onErrorContainer = Crimson700,
    background = Slate50,
    onBackground = Slate900,
    surface = PureWhite,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200
)

@Composable
fun JambGeniusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branded aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
