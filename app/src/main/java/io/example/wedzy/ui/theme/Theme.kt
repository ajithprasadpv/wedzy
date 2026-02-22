package io.example.wedzy.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Extended colors for premium UI elements
data class ExtendedColors(
    val gradientStart: Color,
    val gradientEnd: Color,
    val shimmer: Color,
    val cardGlow: Color,
    val accentGold: Color,
    val softOverlay: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        gradientStart = Blush300,
        gradientEnd = Lavender300,
        shimmer = Color.White.copy(alpha = 0.3f),
        cardGlow = Blush200.copy(alpha = 0.5f),
        accentGold = RoseGold,
        softOverlay = Color.Black.copy(alpha = 0.05f)
    )
}

val LightExtendedColors = ExtendedColors(
    gradientStart = Blush300,
    gradientEnd = Lavender300,
    shimmer = Color.White.copy(alpha = 0.5f),
    cardGlow = Blush100.copy(alpha = 0.8f),
    accentGold = RoseGold,
    softOverlay = Color.Black.copy(alpha = 0.03f)
)

val DarkExtendedColors = ExtendedColors(
    gradientStart = Blush600,
    gradientEnd = Lavender600,
    shimmer = Color.White.copy(alpha = 0.1f),
    cardGlow = Blush800.copy(alpha = 0.5f),
    accentGold = RoseGoldLight,
    softOverlay = Color.White.copy(alpha = 0.03f)
)

// Dark theme with soft, romantic tones
private val DarkColorScheme = darkColorScheme(
    primary = Blush400,
    onPrimary = Color.White,
    primaryContainer = Blush800,
    onPrimaryContainer = Blush100,
    secondary = Lavender400,
    onSecondary = Color.White,
    secondaryContainer = Lavender700,
    onSecondaryContainer = Lavender100,
    tertiary = Coral400,
    onTertiary = Color.White,
    tertiaryContainer = Coral600,
    onTertiaryContainer = Coral100,
    background = Color(0xFF1C1518),
    onBackground = Blush100,
    surface = Color(0xFF251D20),
    onSurface = Blush100,
    surfaceVariant = Color(0xFF3D3235),
    onSurfaceVariant = Blush200,
    error = ErrorRed,
    onError = Color.White
)

// Light theme - Bright, feminine, Gen Z friendly
private val LightColorScheme = lightColorScheme(
    primary = Blush500,
    onPrimary = Color.White,
    primaryContainer = Blush100,
    onPrimaryContainer = Blush800,
    secondary = Lavender500,
    onSecondary = Color.White,
    secondaryContainer = Lavender100,
    onSecondaryContainer = Lavender700,
    tertiary = Coral500,
    onTertiary = Color.White,
    tertiaryContainer = Coral100,
    onTertiaryContainer = Coral600,
    background = Cream,
    onBackground = Neutral800,
    surface = Color.White,
    onSurface = Neutral800,
    surfaceVariant = Blush50,
    onSurfaceVariant = Neutral600,
    error = ErrorRed,
    onError = Color.White,
    outline = Blush200,
    outlineVariant = Blush100
)

@Composable
fun WedzyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = WedzyShapes,
            content = content
        )
    }
}

// Extension to access extended colors easily
object WedzyTheme {
    val extendedColors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}