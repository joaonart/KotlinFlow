package io.github.kotlinflow.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.github.kotlinflow.models.Viewport

/**
 * Renders content at a specific flow (canvas) coordinate position.
 * Content placed inside ViewportPortal transforms with viewport pan and zoom.
 */
@Composable
fun ViewportPortal(
    viewport: Viewport,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.graphicsLayer {
            scaleX = viewport.zoom
            scaleY = viewport.zoom
            translationX = viewport.x
            translationY = viewport.y
        }
    ) {
        content()
    }
}
