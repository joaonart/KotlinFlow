package io.github.kotlinflow.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kotlinflow.types.EdgePathResult

/**
 * A building block for creating custom edge views.
 *
 * [BaseEdge] renders a path with configurable stroke style, animated dashes,
 * and optionally shows a label at the midpoint.
 */
@Composable
fun BaseEdge(
    path: Path,
    modifier: Modifier = Modifier,
    color: Color = Color.Gray.copy(alpha = 0.6f),
    strokeWidth: Float = 2f,
    animated: Boolean = false,
    dashPhase: Float = 0f,
    label: String? = null,
    labelPosition: Offset? = null,
    labelTextStyle: TextStyle = TextStyle(fontSize = 11.sp),
    labelColor: Color = Color.Black,
    labelBackgroundColor: Color = Color.White
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pathEffect = if (animated) {
                PathEffect.dashPathEffect(floatArrayOf(20f, 10f), dashPhase)
            } else null

            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = pathEffect
                )
            )
        }

        if (label != null && labelPosition != null) {
            EdgeText(
                label = label,
                modifier = Modifier.offset(
                    x = labelPosition.x.dp,
                    y = labelPosition.y.dp
                ),
                textStyle = labelTextStyle,
                textColor = labelColor,
                backgroundColor = labelBackgroundColor
            )
        }
    }
}

/**
 * Convenience overload accepting an [EdgePathResult].
 */
@Composable
fun BaseEdge(
    pathResult: EdgePathResult,
    modifier: Modifier = Modifier,
    color: Color = Color.Gray.copy(alpha = 0.6f),
    strokeWidth: Float = 2f,
    animated: Boolean = false,
    dashPhase: Float = 0f,
    label: String? = null,
    labelTextStyle: TextStyle = TextStyle(fontSize = 11.sp),
    labelColor: Color = Color.Black,
    labelBackgroundColor: Color = Color.White
) {
    BaseEdge(
        path = pathResult.path,
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth,
        animated = animated,
        dashPhase = dashPhase,
        label = label,
        labelPosition = Offset(pathResult.labelX, pathResult.labelY),
        labelTextStyle = labelTextStyle,
        labelColor = labelColor,
        labelBackgroundColor = labelBackgroundColor
    )
}
