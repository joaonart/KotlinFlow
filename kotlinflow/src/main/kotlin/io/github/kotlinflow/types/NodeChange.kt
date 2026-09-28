package io.github.kotlinflow.types

import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition

/**
 * Describes a mutation to a node in the graph.
 * Apply changes via [io.github.kotlinflow.utils.applyNodeChanges].
 */
sealed interface NodeChange<out NodeData> {
    data class Position(val id: String, val position: XYPosition) : NodeChange<Nothing>
    data class Selection(val id: String, val selected: Boolean) : NodeChange<Nothing>
    data class Remove(val id: String) : NodeChange<Nothing>
    data class Add<NodeData>(val item: Node<NodeData>) : NodeChange<NodeData>
    data class Dimensions(val id: String, val width: Float, val height: Float) : NodeChange<Nothing>
    data class Replace<NodeData>(val id: String, val item: Node<NodeData>) : NodeChange<NodeData>
}
