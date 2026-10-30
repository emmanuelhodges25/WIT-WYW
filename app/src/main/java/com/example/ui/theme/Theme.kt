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
    primary = WaveGold,
    onPrimary = WaveNavyDark,
    primaryContainer = WaveNavyLight,
    onPrimaryContainer = WaveGoldLight,
    secondary = WaveCyan,
    onSecondary = WaveNavyDark,
    background = WaveNavyDark,
    surface = WaveSurfaceDark,
    surfaceVariant = WaveCardDark,
    onBackground = WaveTextDark,
    onSurface = WaveTextDark,
    outline = Color(0xFF334E68)
)

private val LightColorScheme = lightColorScheme(
    primary = WaveNavy,
    onPrimary = Color.White,
    primaryContainer = WaveGold,
    onPrimaryContainer = WaveNavyDark,
    secondary = WaveCyan,
    onSecondary = Color.White,
    background = WaveParchment,
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = WaveTextPrimary,
    onSurface = WaveTextPrimary,
    outline = WaveBorder
)

@Composable
fun WriteYourWaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep brand identity solid
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
