package io.github.kotlinflow.utils

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.Node

/**
 * Returns nodes that have edges pointing to the given node (predecessors).
 */
fun <NodeData, EdgeData> getIncomers(
    node: Node<NodeData>,
    nodes: List<Node<NodeData>>,
    edges: List<Edge<EdgeData>>
): List<Node<NodeData>> {
    val sourceIds = edges.filter { it.target == node.id }.map { it.source }.toSet()
    return nodes.filter { sourceIds.contains(it.id) }
}

/**
 * Returns nodes that the given node has edges pointing to (successors).
 */
fun <NodeData, EdgeData> getOutgoers(
    node: Node<NodeData>,
    nodes: List<Node<NodeData>>,
    edges: List<Edge<EdgeData>>
): List<Node<NodeData>> {
    val targetIds = edges.filter { it.source == node.id }.map { it.target }.toSet()
    return nodes.filter { targetIds.contains(it.id) }
}

/**
 * Returns all edges connected to the given node (as source or target).
 */
fun <NodeData, EdgeData> getConnectedEdges(
    node: Node<NodeData>,
    edges: List<Edge<EdgeData>>
): List<Edge<EdgeData>> {
    return edges.filter { it.source == node.id || it.target == node.id }
}

/**
 * Returns all edges connected to any of the given nodes.
 */
fun <NodeData, EdgeData> getConnectedEdges(
    nodes: List<Node<NodeData>>,
    edges: List<Edge<EdgeData>>
): List<Edge<EdgeData>> {
    val nodeIds = nodes.map { it.id }.toSet()
    return edges.filter { nodeIds.contains(it.source) || nodeIds.contains(it.target) }
}

data class ElementsToDeleteResult<NodeData, EdgeData>(
    val nodes: List<Node<NodeData>>,
    val edges: List<Edge<EdgeData>>
)

/**
 * Resolves deletable nodes and explicitly requested edges, including every
 * incident edge so deleting a node cannot leave dangling connections.
 */
fun <N, E> elementsToDelete(
    nodes: List<Node<N>>,
    edges: List<Edge<E>>,
    nodeIds: List<String>,
    edgeIds: List<String>
): ElementsToDeleteResult<N, E> {
    val requestedNodes = nodeIds.toSet()
    val requestedEdges = edgeIds.toSet()
    val deletedNodes = nodes.filter { requestedNodes.contains(it.id) && it.deletable }
    val deletedIds = deletedNodes.map { it.id }.toSet()
    val deletedEdges = edges.filter {
        (requestedEdges.contains(it.id) && it.deletable) ||
                deletedIds.contains(it.source) ||
                deletedIds.contains(it.target)
    }
    return ElementsToDeleteResult(deletedNodes, deletedEdges)
}
