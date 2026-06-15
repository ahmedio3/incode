package com.incode.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val IncodeDarkColorScheme = darkColorScheme(
    primary = IncodePrimary,
    onPrimary = IncodeOnPrimary,
    primaryContainer = IncodePrimaryVariant,
    secondary = IncodeSecondary,
    onSecondary = IncodeOnSecondary,
    secondaryContainer = IncodeSecondaryVariant,
    tertiary = IncodeTertiary,
    tertiaryContainer = IncodeTertiaryVariant,
    background = IncodeBackground,
    onBackground = IncodeTextPrimary,
    surface = IncodeSurface,
    onSurface = IncodeTextPrimary,
    surfaceVariant = IncodeSurfaceVariant,
    onSurfaceVariant = IncodeTextSecondary,
    outline = IncodeOutline,
    outlineVariant = IncodeOutlineVariant,
    error = IncodeError,
    onError = IncodeOnPrimary,
    errorContainer = IncodeErrorContainer,
    inverseSurface = IncodeSurfaceHigh,
    inverseOnSurface = IncodeTextPrimary
)

@Composable
fun IncodeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IncodeDarkColorScheme,
        typography = IncodeTypography,
        content = content
    )
}
