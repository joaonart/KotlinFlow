package io.github.kotlinflow.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import io.github.kotlinflow.state.LocalKotlinFlowState
import kotlin.math.max

/**
 * A container for rendering edge labels at a specific position in flow coordinates.
 *
 * Labels are automatically scaled by the inverse of the viewport zoom so they
 * maintain a consistent screen size regardless of the current zoom level.
 */
@Composable
fun EdgeLabelRenderer(
    position: Offset,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val flowState = LocalKotlinFlowState.current
    val zoom = max(flowState.viewport.zoom, 0.01f)

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = 1f / zoom
            scaleY = 1f / zoom
            translationX = position.x
            translationY = position.y
        }
    ) {
        content()
    }
}
