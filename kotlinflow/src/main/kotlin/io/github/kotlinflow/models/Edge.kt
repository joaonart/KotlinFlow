package io.github.kotlinflow.models

import io.github.kotlinflow.types.EdgeStyle
import kotlinx.serialization.Serializable

/**
 * Typealias providing parity with SwiftFlow.FlowEdge.
 */
typealias FlowEdge<EdgeData> = Edge<EdgeData>

/**
 * The path shape used to render an edge between two nodes.
 */
@Serializable
enum class EdgeType {
    DEFAULT,
    BEZIER,
    STRAIGHT,
    STEP,
    SMOOTHSTEP,
    SIMPLEBEZIER
}

/**
 * The visual style of an edge endpoint marker.
 */
@Serializable
enum class MarkerType {
    ARROW,
    ARROW_CLOSED
}

/**
 * Configuration for a marker rendered at an edge endpoint.
 */
@Serializable
data class EdgeMarker(
    val type: MarkerType,
    val width: Float = 12f,
    val height: Float = 12f
) {
    companion object {
        val Arrow = EdgeMarker(type = MarkerType.ARROW)
        val ArrowClosed = EdgeMarker(type = MarkerType.ARROW_CLOSED)
    }
}

/**
 * An empty data type for edges that don't need custom data.
 */
@Serializable
class EmptyEdgeData {
    override fun equals(other: Any?): Boolean = other is EmptyEdgeData
    override fun hashCode(): Int = 0
    override fun toString(): String = "EmptyEdgeData"
}

/**
 * A connection between two nodes in the graph.
 *
 * Edges connect a source node/handle to a target node/handle and support
 * multiple visual styles including different path types, labels, markers,
 * and animation.
 */
@Serializable
data class Edge<EdgeData>(
    val id: String,
    val source: String,
    val target: String,
    val sourceHandle: String? = null,
    val targetHandle: String? = null,
    val type: EdgeType = EdgeType.DEFAULT,
    val selected: Boolean = false,
    val hidden: Boolean = false,
    val label: String? = null,
    val animated: Boolean = false,
    val markerStart: EdgeMarker? = null,
    val markerEnd: EdgeMarker? = null,
    val zIndex: Int = 0,
    val reconnectable: Boolean = false,
    val deletable: Boolean = true,
    val focusable: Boolean = true,
    val interactionWidth: Float = 20f,
    val data: EdgeData? = null,
    val style: EdgeStyle? = null
)
