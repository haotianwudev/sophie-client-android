package com.example.sophieaianalyst.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Professional SOPHIE color scheme
private val LightColorScheme = lightColorScheme(
    primary = SophiePrimary,
    onPrimary = SophieOnPrimary,
    primaryContainer = SophiePrimaryContainer,
    onPrimaryContainer = SophieOnPrimaryContainer,
    secondary = SophieSecondary,
    onSecondary = SophieOnSecondary,
    secondaryContainer = SophieSecondaryContainer,
    onSecondaryContainer = SophieOnSecondaryContainer,
    tertiary = SophieTertiary,
    onTertiary = SophieOnTertiary,
    tertiaryContainer = SophieTertiaryContainer,
    onTertiaryContainer = SophieOnTertiaryContainer,
    error = SophieError,
    onError = SophieOnError,
    errorContainer = SophieErrorContainer,
    onErrorContainer = SophieOnErrorContainer,
    background = SophieBackground,
    onBackground = SophieOnBackground,
    surface = SophieSurface,
    onSurface = SophieOnSurface,
    surfaceVariant = SophieSurfaceVariant,
    onSurfaceVariant = SophieOnSurfaceVariant,
    outline = SophieOutline,
    outlineVariant = SophieOutlineVariant,
    scrim = SophieScrim
)

private val DarkColorScheme = darkColorScheme(
    primary = SophiePrimaryDark,
    onPrimary = SophieOnPrimaryDark,
    primaryContainer = SophiePrimaryContainerDark,
    onPrimaryContainer = SophieOnPrimaryContainerDark,
    secondary = SophieSecondaryDark,
    onSecondary = SophieOnSecondaryDark,
    secondaryContainer = SophieSecondaryContainerDark,
    onSecondaryContainer = SophieOnSecondaryContainerDark,
    tertiary = SophieTertiaryDark,
    onTertiary = SophieOnTertiaryDark,
    tertiaryContainer = SophieTertiaryContainerDark,
    onTertiaryContainer = SophieOnTertiaryContainerDark,
    error = SophieErrorDark,
    onError = SophieOnErrorDark,
    errorContainer = SophieErrorContainerDark,
    onErrorContainer = SophieOnErrorContainerDark,
    background = SophieBackgroundDark,
    onBackground = SophieOnBackgroundDark,
    surface = SophieSurfaceDark,
    onSurface = SophieOnSurfaceDark,
    surfaceVariant = SophieSurfaceVariantDark,
    onSurfaceVariant = SophieOnSurfaceVariantDark,
    outline = SophieOutlineDark,
    outlineVariant = SophieOutlineVariantDark,
    scrim = SophieScrimDark
)

@Composable
fun SophieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SophieTypography,
        shapes = SophieShapes,
        content = content
    )
} 