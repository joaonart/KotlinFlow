package io.github.kotlinflow.models

/**
 * An internal representation of a node with computed layout properties.
 *
 * [InternalNode] wraps a [Node] with its measured dimensions and absolute
 * position (accounting for parent hierarchy). Used for performance and
 * clean API boundaries.
 */
data class InternalNode<NodeData>(
    val node: Node<NodeData>,
    val absolutePosition: XYPosition = XYPosition.Zero,
    val measuredWidth: Float? = null,
    val measuredHeight: Float? = null
) {
    val id: String get() = node.id
}
