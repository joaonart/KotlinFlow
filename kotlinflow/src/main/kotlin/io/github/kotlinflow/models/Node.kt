package io.github.kotlinflow.models

import io.github.kotlinflow.types.NodeExtent
import io.github.kotlinflow.types.NodeOrigin
import io.github.kotlinflow.types.NodeStyle
import io.github.kotlinflow.types.Position
import kotlinx.serialization.Serializable

/**
 * A graph node with generic user data, position, and interaction properties.
 *
 * [Node] is generic over [NodeData], allowing you to attach any data model
 * to nodes.
 */
@Serializable
data class Node<NodeData>(
    val id: String,
    val position: XYPosition,
    val data: NodeData,
    val type: String = "default",
    val parentId: String? = null,
    val selected: Boolean = false,
    val hidden: Boolean = false,
    val width: Float? = null,
    val height: Float? = null,
    val draggable: Boolean = true,
    val selectable: Boolean = true,
    val connectable: Boolean = true,
    val deletable: Boolean = true,
    val expandable: Boolean = false,
    val expanded: Boolean = true,
    val expandParent: Boolean = false,
    val focusable: Boolean = true,
    val zIndex: Int = 0,
    val origin: NodeOrigin = NodeOrigin.TopLeft,
    val sourcePosition: Position? = null,
    val targetPosition: Position? = null,
    val extent: NodeExtent? = null,
    val style: NodeStyle? = null
)
