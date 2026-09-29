package io.github.kotlinflow.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.kotlinflow.state.LocalKotlinFlowState
import io.github.kotlinflow.types.BackgroundVariant
import kotlin.math.max

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
    gap: Float = 24f,
    size: Float = 1.5f,
    modifier: Modifier = Modifier
) {
    val flowState = LocalKotlinFlowState.current
    val density = LocalDensity.current
    val gapPx = remember(gap, density) {
        with(density) { gap.dp.toPx() }
    }
    val sizePx = remember(size, density) {
        with(density) { size.dp.toPx() }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val vp = flowState.viewport
        val zoom = vp.zoom
        val spacing = gapPx * zoom
        if (spacing > 6f) {
            val startX = (vp.x % spacing + spacing) % spacing
            val startY = (vp.y % spacing + spacing) % spacing

            when (variant) {
                BackgroundVariant.NONE -> {
                    // No background pattern rendered
                }
                BackgroundVariant.DOTS -> {
                    val radius = sizePx * zoom / 2f
                    val dotDiameter = max(radius * 2f, 1.5f)
                    val points = ArrayList<Offset>(((this.size.width / spacing) + 2).toInt() * ((this.size.height / spacing) + 2).toInt())
                    var x = startX
                    while (x <= this.size.width) {
                        var y = startY
                        while (y <= this.size.height) {
                            points.add(Offset(x, y))
                            y += spacing
                        }
                        x += spacing
                    }
                    drawPoints(
                        points = points,
                        pointMode = PointMode.Points,
                        color = color,
                        strokeWidth = dotDiameter,
                        cap = StrokeCap.Round
                    )
                }
                BackgroundVariant.LINES -> {
                    var x = startX
                    while (x <= this.size.width) {
                        drawLine(color = color, start = Offset(x, 0f), end = Offset(x, this.size.height), strokeWidth = 1f)
                        x += spacing
                    }
                    var y = startY
                    while (y <= this.size.height) {
                        drawLine(color = color, start = Offset(0f, y), end = Offset(this.size.width, y), strokeWidth = 1f)
                        y += spacing
                    }
                }
                BackgroundVariant.CROSS -> {
                    val arm = 3f * zoom
                    var x = startX
                    while (x <= this.size.width) {
                        var y = startY
                        while (y <= this.size.height) {
                            drawLine(color = color, start = Offset(x - arm, y), end = Offset(x + arm, y), strokeWidth = 1f)
                            drawLine(color = color, start = Offset(x, y - arm), end = Offset(x, y + arm), strokeWidth = 1f)
                            y += spacing
                        }
                        x += spacing
                    }
                }
            }
        }
    }
}
