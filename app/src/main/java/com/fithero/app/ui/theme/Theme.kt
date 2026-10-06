package com.fithero.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun FitHeroTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = Purple,
        secondary = PurpleLight,
        tertiary = Amber,
        background = Background,
        surface = White,
        onPrimary = White,
        onSecondary = White,
        onTertiary = White,
        onBackground = Gray900,
        onSurface = Gray900
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
