package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = NeonBlue,
    secondary = NeonPurple,
    tertiary = TextGreen,
    background = TerminalDarker,
    surface = DarkSurface,
    onPrimary = TerminalDarker,
    onSecondary = TerminalDarker,
    onTertiary = TerminalDarker,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = DarkSurfaceHighlight,
    onSurfaceVariant = TextGray
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  // The darkTheme/dynamicColor parameters were accepted but ignored (the scheme
  // is hardcoded by design); they are removed so the signature stops lying.
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
