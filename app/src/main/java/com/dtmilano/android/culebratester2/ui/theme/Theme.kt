package com.dtmilano.android.culebratester2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

//    <color name="colorPrimary">#607D8B</color>
//    <color name="colorPrimaryDark">#455A64</color>
//    <color name="colorPrimaryLight">#CFD8DC</color>
//    <color name="colorAccent">#009688</color>
//    <color name="colorPrimaryText">#212121</color>
//    <color name="colorSecondaryText">#727272</color>
//    <color name="colorIcons">#FFFFFF</color>
//    <color name="colorDivider">#B6B6B6</color>

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Primary,
    onSurface = Accent
)

private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = PrimaryDark,
    secondary = PrimaryLight,
    background = Primary,
    onSurface = Accent

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun CulebraTester2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}