package io.github.kotlinflow.utils

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.Viewport
import kotlin.math.max
import kotlin.math.min

/**
 * Returns the bounding box containing all visible nodes.
 */
fun <T> getNodesBounds(
    nodes: List<Node<T>>,
    nodeSizes: Map<String, Size>
): Rect {
    val visible = nodes.filter { !it.hidden }
    if (visible.isEmpty()) return Rect.Zero

    var minX = Float.POSITIVE_INFINITY
    var minY = Float.POSITIVE_INFINITY
    var maxX = Float.NEGATIVE_INFINITY
    var maxY = Float.NEGATIVE_INFINITY

    for (node in visible) {
        val size = nodeSizes[node.id] ?: Size(node.width ?: 200f, node.height ?: 100f)
        minX = min(minX, node.position.x)
        minY = min(minY, node.position.y)
        maxX = max(maxX, node.position.x + size.width)
        maxY = max(maxY, node.position.y + size.height)
    }

    if (minX == Float.POSITIVE_INFINITY) return Rect.Zero
    return Rect(left = minX, top = minY, right = maxX, bottom = maxY)
}

/**
 * Returns the viewport needed to display the given bounds within a viewport size.
 */
fun getViewportForBounds(
    bounds: Rect,
    viewportSize: Size,
    minZoom: Float = Viewport.MinZoom,
    maxZoom: Float = Viewport.MaxZoom,
    padding: Float = 80f
): Viewport {
    val cw = max(bounds.width, 1f)
    val ch = max(bounds.height, 1f)
    val effectiveWidth = max(1f, viewportSize.width - padding * 2f)
    val effectiveHeight = max(1f, viewportSize.height - padding * 2f)
    val zoom = min(effectiveWidth / cw, effectiveHeight / ch).coerceIn(minZoom, maxZoom)
    val midX = (bounds.left + bounds.right) / 2f
    val midY = (bounds.top + bounds.bottom) / 2f

    return Viewport(
        x = viewportSize.width / 2f - midX * zoom,
        y = viewportSize.height / 2f - midY * zoom,
        zoom = zoom
    )
}
