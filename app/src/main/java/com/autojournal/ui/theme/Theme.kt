package com.autojournal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ===== ЦВЕТА (Metal Chrome Theme) =====
object MetalChromeTheme {
    // Металлические оттенки
    val Steel = Color(0xFFB0B5B9)
    val DarkSteel = Color(0xFF4A4E52)
    val Chrome = Color(0xFFD4D9DD)
    val Titanium = Color(0xFF8A8F94)
    val BrushedMetal = Color(0xFFC8CDD2)

    // Фоны
    val FactoryDark = Color(0xFF1A1C1E)
    val MetalGrey = Color(0xFF2D3033)
    val AnodizedBlack = Color(0xFF0D0E0F)
    val WorkshopFloor = Color(0xFF25282B)

    // Акценты
    val GoldAccent = Color(0xFFC9A84C)
    val GoldLight = Color(0xFFD4B86A)
    val RustOrange = Color(0xFFD4773A)
    val OilBlue = Color(0xFF2A6F8F)
    val Copper = Color(0xFFB87333)
    val WeldingYellow = Color(0xFFFFD700)

    // Текст
    val MetalText = Color(0xFFF5F5F5)
    val DarkMetalText = Color(0xFF8A8F94)
    val TextHint = Color(0xFF666666)

    // Статусы
    val StatusGreen = Color(0xFF4CAF50)
    val StatusRed = Color(0xFFF44336)
    val StatusYellow = Color(0xFFFFC107)
    val StatusBlue = Color(0xFF2196F3)
}

// ===== ТИПОГРАФИКА =====
val AutoJournalTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 57.sp, lineHeight = 64.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.5.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp)
)

// ===== ЕДИНСТВЕННАЯ ФУНКЦИЯ ТЕМЫ =====
@Composable
fun AutoJournalTheme(
    darkTheme: Boolean = true, // Всегда тёмная тема для индустриального стиля
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = MetalChromeTheme.GoldAccent,
        onPrimary = MetalChromeTheme.AnodizedBlack,
        primaryContainer = MetalChromeTheme.DarkSteel,
        secondary = MetalChromeTheme.Steel,
        onSecondary = MetalChromeTheme.AnodizedBlack,
        tertiary = MetalChromeTheme.Titanium,
        background = MetalChromeTheme.FactoryDark,
        onBackground = MetalChromeTheme.MetalText,
        surface = MetalChromeTheme.MetalGrey,
        onSurface = MetalChromeTheme.MetalText,
        error = MetalChromeTheme.StatusRed,
        onError = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AutoJournalTypography,
        content = content
    )
}