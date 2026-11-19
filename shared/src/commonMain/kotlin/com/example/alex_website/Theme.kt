package com.example.alex_website

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import alex_website.shared.generated.resources.Res
import alex_website.shared.generated.resources.inter
import org.jetbrains.compose.resources.Font

// Light: warm "paper" background with a muted teal accent.
private val LightColors = lightColorScheme(
    primary = Color(0xFF2F6F73),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFF6F3EC),
    onBackground = Color(0xFF2B2A27),
    surface = Color(0xFFEFEBE2),
    onSurface = Color(0xFF2B2A27),
    onSurfaceVariant = Color(0xFF6B675F),
    outlineVariant = Color(0xFFDCD6CA),
)

// Dark: soft slate (not pure black) with a frosty blue accent.
private val DarkColors = darkColorScheme(
    primary = Color(0xFF88C0D0),
    onPrimary = Color(0xFF1B2B33),
    background = Color(0xFF1F232A),
    onBackground = Color(0xFFD8DEE9),
    surface = Color(0xFF282D35),
    onSurface = Color(0xFFD8DEE9),
    onSurfaceVariant = Color(0xFF9AA3B2),
    outlineVariant = Color(0xFF3A404B),
)

@OptIn(ExperimentalTextApi::class)
@Composable
private fun interFamily(): FontFamily {
    // Inter is a variable font: one file, each weight selected via the 'wght' axis.
    val fonts = listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold).map { w ->
        Font(Res.font.inter, weight = w, variationSettings = FontVariation.Settings(FontVariation.weight(w.weight)))
    }
    return FontFamily(fonts)
}

@Composable
fun SiteTheme(dark: Boolean, content: @Composable () -> Unit) {
    val inter = interFamily()
    val base = Typography()
    val typography = Typography(
        displayLarge = base.displayLarge.copy(fontFamily = inter),
        displayMedium = base.displayMedium.copy(fontFamily = inter),
        displaySmall = base.displaySmall.copy(fontFamily = inter),
        headlineLarge = base.headlineLarge.copy(fontFamily = inter),
        headlineMedium = base.headlineMedium.copy(fontFamily = inter),
        headlineSmall = base.headlineSmall.copy(fontFamily = inter),
        titleLarge = base.titleLarge.copy(fontFamily = inter),
        titleMedium = base.titleMedium.copy(fontFamily = inter),
        titleSmall = base.titleSmall.copy(fontFamily = inter),
        bodyLarge = base.bodyLarge.copy(fontFamily = inter, letterSpacing = 0.sp),
        bodyMedium = base.bodyMedium.copy(fontFamily = inter),
        bodySmall = base.bodySmall.copy(fontFamily = inter),
        labelLarge = base.labelLarge.copy(fontFamily = inter),
        labelMedium = base.labelMedium.copy(fontFamily = inter),
        labelSmall = base.labelSmall.copy(fontFamily = inter),
    )
    MaterialTheme(colorScheme = if (dark) DarkColors else LightColors, typography = typography, content = content)
}
