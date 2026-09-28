package io.github.kotlinflow.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.Viewport
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.types.FitViewOptions
import kotlin.math.max
import kotlin.math.min

typealias SwiftFlowInstance = KotlinFlowInstance

/**
 * Programmatic controller for the canvas viewport and graph state queries.
 */
class KotlinFlowInstance {
    var viewport: Viewport by mutableStateOf(Viewport.Identity)
    var viewSize: Size by mutableStateOf(Size.Zero)
    var nodeSizes: Map<String, Size> by mutableStateOf(emptyMap())

    var onViewportChange: ((Viewport) -> Unit)? = null

    // Internal closures set by KotlinFlow canvas
    internal var internalApplyViewport: ((Viewport, Boolean) -> Unit)? = null
    internal var internalDeleteElements: ((List<String>, List<String>) -> Unit)? = null

    /**
     * Returns the current viewport.
     */
    @JvmName("getCurrentViewport")
    fun getViewport(): Viewport = viewport

    /**
     * Sets the viewport directly.
     */
    fun setViewport(vp: Viewport, animated: Boolean = true) {
        val clamped = vp.copy(zoom = vp.clampedZoom)
        viewport = clamped
        internalApplyViewport?.invoke(clamped, animated)
        onViewportChange?.invoke(clamped)
    }

    /**
     * Adjusts the viewport to frame all (or specific) nodes.
     */
    fun <T> fitView(
        nodes: List<Node<T>>,
        nodeSizes: Map<String, Size> = this.nodeSizes,
        options: FitViewOptions = FitViewOptions()
    ) {
        val targetNodes: List<Node<T>> = if (options.nodeIds != null) {
            val idSet = options.nodeIds.toSet()
            nodes.filter { it.id in idSet }
        } else {
            if (options.includeHiddenNodes) nodes else nodes.filter { !it.hidden }
        }

        if (targetNodes.isEmpty()) return

        val bounds = computeBounds(targetNodes, nodeSizes)
        val cw = bounds.width
        val ch = bounds.height
        if (cw <= 0f || ch <= 0f) return

        val padding = options.padding
        val effectiveWidth = max(1f, viewSize.width - padding * 2f)
        val effectiveHeight = max(1f, viewSize.height - padding * 2f)
        val zoom = min(effectiveWidth / cw, effectiveHeight / ch).coerceIn(options.minZoom, options.maxZoom)

        val midX = (bounds.left + bounds.right) / 2f
        val midY = (bounds.top + bounds.bottom) / 2f

        val vp = Viewport(
            x = viewSize.width / 2f - midX * zoom,
            y = viewSize.height / 2f - midY * zoom,
            zoom = zoom
        )
        setViewport(vp, animated = (options.durationMillis ?: 0L) > 0L)
    }

    /**
     * Centers the viewport on a canvas coordinate.
     */
    fun setCenter(x: Float, y: Float, zoom: Float? = null, animated: Boolean = true) {
        val z = zoom ?: viewport.zoom
        val vp = Viewport(
            x = -x * z + viewSize.width / 2f,
            y = -y * z + viewSize.height / 2f,
            zoom = z
        )
        setViewport(vp, animated = animated)
    }

    /**
     * Sets the zoom level, preserving the current center point.
     */
    fun zoomTo(zoom: Float, animated: Boolean = true) {
        val currentZoom = max(viewport.zoom, Viewport.MinZoom)
        val centerX = (viewSize.width / 2f - viewport.x) / currentZoom
        val centerY = (viewSize.height / 2f - viewport.y) / currentZoom
        val z = zoom.coerceIn(Viewport.MinZoom, Viewport.MaxZoom)
        val vp = Viewport(
            x = viewSize.width / 2f - centerX * z,
            y = viewSize.height / 2f - centerY * z,
            zoom = z
        )
        setViewport(vp, animated = animated)
    }

    /** Zooms in by 25%. */
    fun zoomIn(animated: Boolean = true) {
        zoomTo(viewport.zoom * 1.25f, animated = animated)
    }

    /** Zooms out by 25%. */
    fun zoomOut(animated: Boolean = true) {
        zoomTo(viewport.zoom / 1.25f, animated = animated)
    }

    /** Resets viewport to origin at 100% zoom. */
    fun reset(animated: Boolean = true) {
        setViewport(Viewport.Identity, animated = animated)
    }

    // MARK: - Coordinate Conversion

    /** Converts a screen-space coordinate to flow (canvas) coordinates. */
    fun screenToFlowPosition(screenPoint: Offset): Offset {
        val zoom = max(viewport.zoom, Viewport.MinZoom)
        return Offset(
            x = (screenPoint.x - viewport.x) / zoom,
            y = (screenPoint.y - viewport.y) / zoom
        )
    }

    /** Converts a flow (canvas) coordinate to screen-space coordinate. */
    fun flowToScreenPosition(flowPoint: Offset): Offset {
        return Offset(
            x = flowPoint.x * viewport.zoom + viewport.x,
            y = flowPoint.y * viewport.zoom + viewport.y
        )
    }

    // MARK: - Graph State Access

    /** Deletes the specified nodes and edges by ID. */
    fun deleteElements(nodeIds: List<String> = emptyList(), edgeIds: List<String> = emptyList()) {
        internalDeleteElements?.invoke(nodeIds, edgeIds)
    }

    /** Returns bounding box for the given nodes. */
    fun <T> getNodesBounds(nodes: List<Node<T>>, nodeSizes: Map<String, Size> = this.nodeSizes): Rect {
        return io.github.kotlinflow.utils.getNodesBounds(nodes, nodeSizes)
    }

    /** Returns the viewport needed to display the given bounds. */
    fun getViewportForBounds(
        bounds: Rect,
        minZoom: Float = Viewport.MinZoom,
        maxZoom: Float = Viewport.MaxZoom,
        padding: Float = 80f
    ): Viewport {
        return io.github.kotlinflow.utils.getViewportForBounds(
            bounds = bounds,
            viewportSize = viewSize,
            minZoom = minZoom,
            maxZoom = maxZoom,
            padding = padding
        )
    }

    private fun <T> computeBounds(nodes: List<Node<T>>, nodeSizes: Map<String, Size>): Rect {
        return io.github.kotlinflow.utils.getNodesBounds(nodes, nodeSizes)
    }
}
