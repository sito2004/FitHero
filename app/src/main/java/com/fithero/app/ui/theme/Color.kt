package com.fithero.app.ui.theme

import androidx.compose.ui.graphics.Color

val Purple = Color(0xFF7C3AED)
val PurpleLight = Color(0xFFA855F7)
val PurpleDark = Color(0xFF5B21B6)
val PurpleBg = Color(0xFFEDE9FE)
val PurpleBgLight = Color(0xFFF3F0FF)

val Amber = Color(0xFFF59E0B)
val Green = Color(0xFF10B981)
val Red = Color(0xFFEF4444)
val Cyan = Color(0xFF06B6D4)
val Indigo = Color(0xFF6366F1)

val Gray100 = Color(0xFFF3F4F6)
val Gray200 = Color(0xFFE5E7EB)
val Gray400 = Color(0xFF9CA3AF)
val Gray500 = Color(0xFF6B7280)
val Gray700 = Color(0xFF4B5563)
val Gray900 = Color(0xFF1F2937)

val Background = Color(0xFFF8F9FF)
val White = Color(0xFFFFFFFF)
val DarkBg = Color(0xFF0F0F1A)

fun parseColor(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Purple
    }
}
