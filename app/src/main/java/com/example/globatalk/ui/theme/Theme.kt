package com.example.globatalk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GlobeTalkColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = Background,
    surface = CardBg,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    error = Error
)

@Composable
fun GlobeTalkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GlobeTalkColorScheme,
        content = content
    )
}
