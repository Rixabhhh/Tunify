package com.example.tunify.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tunify.R

// 1. Your AI Editorial Font
val EditorialSerif = FontFamily(
    Font(R.font.playfair_regular, FontWeight.Normal),
    Font(R.font.playfair_bold, FontWeight.Bold),
    Font(R.font.playfair_italic, FontWeight.Normal, FontStyle.Italic)
)

// 2. Your New Primary App Font
val PrimaryAppFont = FontFamily(
    Font(R.font.plusjakartasans_semibold, FontWeight.Normal))


// 3. Override the Material Typography
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = PrimaryAppFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PrimaryAppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PrimaryAppFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)