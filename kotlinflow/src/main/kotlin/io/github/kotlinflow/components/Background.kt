package io.github.kotlinflow.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.kotlinflow.state.LocalKotlinFlowState
import io.github.kotlinflow.types.BackgroundVariant

/**
 * A configurable background pattern for the KotlinFlow canvas.
 *
 * Place inside the overlay Composable of KotlinFlow to render a
 * dot, line, or cross pattern that tracks with viewport pan and zoom.
 */
@Composable
fun Background(
    id: String? = null,
    variant: BackgroundVariant = BackgroundVariant.DOTS,
    color: Color = Color.Gray.copy(alpha = 0.3f),
    gap: Float = 20f,
    size: Float = 1.5f,
    modifier: Modifier = Modifier
) {
    val flowState = LocalKotlinFlowState.current

    Canvas(modifier = modifier.fillMaxSize()) {
        val zoom = flowState.viewport.zoom
        val spacing = gap * zoom
        if (spacing > 2f) {
            val startX = (flowState.viewport.x % spacing + spacing) % spacing
            val startY = (flowState.viewport.y % spacing + spacing) % spacing

            when (variant) {
                BackgroundVariant.DOTS -> {
                    val radius = size * zoom / 2f
                    var x = startX
                    while (x <= this.size.width) {
                        var y = startY
                        while (y <= this.size.height) {
                            drawCircle(color = color, radius = radius, center = Offset(x, y))
                            y += spacing
                        }
                        x += spacing
                    }
                }
                BackgroundVariant.LINES -> {
                    var x = startX
                    while (x <= this.size.width) {
                        drawLine(color = color, start = Offset(x, 0f), end = Offset(x, this.size.height), strokeWidth = 0.5f)
                        x += spacing
                    }
                    var y = startY
                    while (y <= this.size.height) {
                        drawLine(color = color, start = Offset(0f, y), end = Offset(this.size.width, y), strokeWidth = 0.5f)
                        y += spacing
                    }
                }
                BackgroundVariant.CROSS -> {
                    val arm = 3f * zoom
                    var x = startX
                    while (x <= this.size.width) {
                        var y = startY
                        while (y <= this.size.height) {
                            drawLine(color = color, start = Offset(x - arm, y), end = Offset(x + arm, y), strokeWidth = 0.5f)
                            drawLine(color = color, start = Offset(x, y - arm), end = Offset(x, y + arm), strokeWidth = 0.5f)
                            y += spacing
                        }
                        x += spacing
                    }
                }
            }
        }
    }
}
