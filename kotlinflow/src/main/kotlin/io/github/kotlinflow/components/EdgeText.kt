package io.github.kotlinflow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A positioned text label for use within custom edge rendering.
 */
@Composable
fun EdgeText(
    label: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium),
    textColor: Color = Color(0xFF1E293B),
    showBackground: Boolean = true,
    backgroundColor: Color = Color.White,
    borderColor: Color? = Color(0xFFCBD5E1),
    borderWidth: Dp = 1.dp,
    backgroundCornerRadius: Dp = 6.dp
) {
    val shape = RoundedCornerShape(backgroundCornerRadius)
    val backgroundModifier = if (showBackground) {
        var m = Modifier
            .shadow(elevation = 1.dp, shape = shape, clip = false)
            .clip(shape)
            .background(backgroundColor)
        if (borderColor != null && borderWidth > 0.dp) {
            m = m.border(width = borderWidth, color = borderColor, shape = shape)
        }
        m.padding(horizontal = 8.dp, vertical = 3.dp)
    } else Modifier

    Box(
        modifier = modifier.then(backgroundModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = textStyle,
            color = textColor
        )
    }
}
