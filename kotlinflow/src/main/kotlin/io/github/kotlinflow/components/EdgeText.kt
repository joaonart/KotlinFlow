package io.github.kotlinflow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    textStyle: TextStyle = TextStyle(fontSize = 11.sp),
    textColor: Color = Color.Black,
    showBackground: Boolean = true,
    backgroundColor: Color = Color.White,
    backgroundCornerRadius: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .then(
                if (showBackground) {
                    Modifier
                        .clip(RoundedCornerShape(backgroundCornerRadius))
                        .background(backgroundColor.copy(alpha = 0.9f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                } else Modifier
            )
    ) {
        Text(
            text = label,
            style = textStyle,
            color = textColor
        )
    }
}
