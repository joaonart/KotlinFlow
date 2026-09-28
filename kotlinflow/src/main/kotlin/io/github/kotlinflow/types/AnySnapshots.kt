package io.github.kotlinflow.types

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EdgeMarker
import io.github.kotlinflow.models.EdgeType
import io.github.kotlinflow.models.Node
import kotlinx.serialization.Serializable

/**
 * A lightweight, type-erased snapshot of a node for use by overlay components
 * like MiniMap that don't need generic node data.
 */
@Serializable
data class AnyNodeSnapshot(
    val id: String,
    val x: Float,
    val y: Float,
    val selected: Boolean,
    val hidden: Boolean,
    val type: String
) {
    companion object {
        fun <T> from(node: Node<T>): AnyNodeSnapshot = AnyNodeSnapshot(
            id = node.id,
            x = node.position.x,
            y = node.position.y,
            selected = node.selected,
            hidden = node.hidden,
            type = node.type
        )
    }
}

/**
 * A lightweight, type-erased snapshot of an edge for use by overlay components
 * like MiniMap that don't need generic edge data.
 */
@Serializable
data class AnyEdgeSnapshot(
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
    val deletable: Boolean = true
) {
    companion object {
        fun <E> from(edge: Edge<E>): AnyEdgeSnapshot = AnyEdgeSnapshot(
            id = edge.id,
            source = edge.source,
            target = edge.target,
            sourceHandle = edge.sourceHandle,
            targetHandle = edge.targetHandle,
            type = edge.type,
            selected = edge.selected,
            hidden = edge.hidden,
            label = edge.label,
            animated = edge.animated,
            markerStart = edge.markerStart,
            markerEnd = edge.markerEnd,
            zIndex = edge.zIndex,
            reconnectable = edge.reconnectable,
            deletable = edge.deletable
        )
    }
}
