package io.github.kotlinflow.utils

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.kotlinflow.models.Node

/**
 * Returns all nodes whose bounding boxes overlap with the given node.
 */
fun <T> getIntersectingNodes(
    node: Node<T>,
    nodes: List<Node<T>>,
    nodeSizes: Map<String, Size> = emptyMap()
): List<Node<T>> {
    val rect = nodeRect(node, nodeSizes)
    return nodes.filter { other ->
        other.id != node.id && !other.hidden && rect.overlaps(nodeRect(other, nodeSizes))
    }
}

/**
 * Returns whether two nodes' bounding boxes overlap.
 */
fun <T> isNodeIntersecting(
    node: Node<T>,
    otherNode: Node<T>,
    nodeSizes: Map<String, Size> = emptyMap()
): Boolean {
    return nodeRect(node, nodeSizes).overlaps(nodeRect(otherNode, nodeSizes))
}

private fun <T> nodeRect(node: Node<T>, nodeSizes: Map<String, Size>): Rect {
    val size = nodeSizes[node.id] ?: Size(node.width ?: 150f, node.height ?: 50f)
    val w = node.width ?: size.width
    val h = node.height ?: size.height
    return Rect(
        left = node.position.x,
        top = node.position.y,
        right = node.position.x + w,
        bottom = node.position.y + h
    )
}
