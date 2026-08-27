package com.fiap.autria.ui.theme

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

// ---------------------------------------------------------------------------
// Slots extras que a tela do AppNew usa (surface, error, containers) e que
// ainda não existiam no seu Theme.kt original. Os hex abaixo são sugestões
// pensadas para combinar com Blue/Orange — ajuste como preferir.
// ---------------------------------------------------------------------------
private val SurfaceDark = Color(0xFF0B0F26)
private val SurfaceVariantDark = Color(0xFF141A3D)
private val OutlineDark = Color(0xFF3A3F5C)

private val SurfaceLight = Color(0xFFFFF8EC)
private val SurfaceVariantLight = Color(0xFFF3E4C8)
private val OutlineLight = Border40

private val ErrorRed = Color(0xFFFF5449)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainerDark = Color(0xFF93000A)
private val ErrorContainerLight = Color(0xFFFFDAD4)

private val DarkColorScheme = darkColorScheme(
    primary = Orange80,
    onPrimary = Color(0xFF3B2400),
    primaryContainer = Orange40,
    onPrimaryContainer = Color.White,
    secondary = Blue80,
    onSecondary = Color(0xFF002153),
    secondaryContainer = Blue40,
    onSecondaryContainer = Color.White,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC8C6D0),
    outline = OutlineDark,
    outlineVariant = OutlineDark.copy(alpha = 0.4f),
    error = ErrorRed,
    onError = OnError,
    errorContainer = ErrorContainerDark,
    onErrorContainer = Color(0xFFFFDAD4),
)

private val LightColorScheme = lightColorScheme(
    primary = Orange40,
    onPrimary = Color.White,
    primaryContainer = Orange80,
    onPrimaryContainer = Color(0xFF3B2400),
    secondary = Blue40,
    onSecondary = Color.White,
    secondaryContainer = Blue80,
    onSecondaryContainer = Color(0xFF002153),
    background = BackgroundLight,
    onBackground = Color.Black,
    surface = SurfaceLight,
    onSurface = Color.Black,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF4B4640),
    outline = OutlineLight,
    outlineVariant = OutlineLight.copy(alpha = 0.5f),
    error = ErrorRed,
    onError = OnError,
    errorContainer = ErrorContainerLight,
    onErrorContainer = Color(0xFF410002),
)

/* Other default colors to override
background = Color(0xFFFFFBFE),
surface = Color(0xFFFFFBFE),
onPrimary = Color.White,
onSecondary = Color.White,
onTertiary = Color.White,
onBackground = Color(0xFF1C1B1F),
onSurface = Color(0xFF1C1B1F),
*/

@Composable
fun AutriaTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}