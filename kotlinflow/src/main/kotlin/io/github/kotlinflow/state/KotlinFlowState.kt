package io.github.kotlinflow.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.kotlinflow.models.Connection
import io.github.kotlinflow.models.KotlinFlowTheme
import io.github.kotlinflow.models.Viewport
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.types.AnyEdgeSnapshot
import io.github.kotlinflow.types.AnyNodeSnapshot
import io.github.kotlinflow.types.ConnectionState
import io.github.kotlinflow.types.HandleType
import kotlin.math.max
import kotlin.math.min

typealias SwiftFlowState = KotlinFlowState

/**
 * Internal observable state shared between KotlinFlow and its overlay components.
 *
 * KotlinFlow creates this object and injects it into CompositionLocal so that
 * Controls, MiniMap, Background, and Panel can access viewport, node sizes,
 * and other canvas state without explicit prop-drilling.
 */
class KotlinFlowState {

    // MARK: - Viewport
    var viewport: Viewport by mutableStateOf(Viewport.Identity)
    var viewSize: Size by mutableStateOf(Size.Zero)

    // MARK: - Node Layout
    var nodeSizes: Map<String, Size> by mutableStateOf(emptyMap())
    var absolutePositions: Map<String, XYPosition> by mutableStateOf(emptyMap())
    var handlePositions: Map<String, Offset> by mutableStateOf(emptyMap())
    var handleTypes: Map<String, HandleType> by mutableStateOf(emptyMap())

    // MARK: - Graph Data (mirrors for overlay components)
    var nodes: List<AnyNodeSnapshot> by mutableStateOf(emptyList())
    var edges: List<AnyEdgeSnapshot> by mutableStateOf(emptyList())

    // MARK: - Connection State
    var activeConnection: ConnectionState? by mutableStateOf(null)

    // MARK: - Connection Queries
    var connectionsMap: Map<String, List<Connection>> by mutableStateOf(emptyMap())

    // MARK: - Interactivity
    var isInteractive: Boolean by mutableStateOf(true)

    // MARK: - Theme
    var theme: KotlinFlowTheme by mutableStateOf(KotlinFlowTheme.Default)

    // MARK: - Viewport Mutation
    internal var internalApplyViewport: ((Viewport, Boolean) -> Unit)? = null

    /** Requests a viewport change with optional animation. */
    fun setViewport(vp: Viewport, animated: Boolean = true) {
        val clamped = vp.copy(zoom = vp.clampedZoom)
        viewport = clamped
        internalApplyViewport?.invoke(clamped, animated)
    }

    /** Zooms in by 25%, preserving the center point. */
    fun zoomIn(animated: Boolean = true) {
        val newZoom = min(viewport.zoom * 1.25f, Viewport.MaxZoom)
        zoomTo(newZoom, animated = animated)
    }

    /** Zooms out by 25%, preserving the center point. */
    fun zoomOut(animated: Boolean = true) {
        val newZoom = max(viewport.zoom / 1.25f, Viewport.MinZoom)
        zoomTo(newZoom, animated = animated)
    }

    /** Sets zoom to a specific level, preserving the center point. */
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

    /** Fits all visible nodes into the viewport. */
    fun fitView(padding: Float = 80f, maxZoom: Float = 1.5f, animated: Boolean = true) {
        val visibleNodes = nodes.filter { !it.hidden }
        if (visibleNodes.isEmpty()) return

        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY

        for (node in visibleNodes) {
            val pos = absolutePositions[node.id] ?: XYPosition(node.x, node.y)
            val size = nodeSizes[node.id] ?: Size(200f, 100f)
            minX = min(minX, pos.x)
            minY = min(minY, pos.y)
            maxX = max(maxX, pos.x + size.width)
            maxY = max(maxY, pos.y + size.height)
        }

        if (minX == Float.POSITIVE_INFINITY) return
        val cw = maxX - minX
        val ch = maxY - minY
        if (cw <= 0f || ch <= 0f) return

        val effectiveWidth = max(1f, viewSize.width - padding * 2f)
        val effectiveHeight = max(1f, viewSize.height - padding * 2f)
        val z = min(min(effectiveWidth / cw, effectiveHeight / ch), maxZoom).coerceIn(Viewport.MinZoom, Viewport.MaxZoom)

        val vp = Viewport(
            x = viewSize.width / 2f - (minX + maxX) / 2f * z,
            y = viewSize.height / 2f - (minY + maxY) / 2f * z,
            zoom = z
        )
        setViewport(vp, animated = animated)
    }
}
