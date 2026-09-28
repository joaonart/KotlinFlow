package io.github.kotlinflow.utils

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.types.EdgeChange
import io.github.kotlinflow.types.NodeChange

/**
 * Applies an array of node changes to a node list and returns the new list.
 *
 * This is the primary state-update function for nodes. Use it in your
 * onNodesChange callback:
 *
 * ```kotlin
 * onNodesChange = { changes ->
 *     nodes = applyNodeChanges(changes, nodes)
 * }
 * ```
 */
fun <T> applyNodeChanges(changes: List<NodeChange<T>>, nodes: List<Node<T>>): List<Node<T>> {
    val result = nodes.toMutableList()
    for (change in changes) {
        when (change) {
            is NodeChange.Position -> {
                val index = result.indexOfFirst { it.id == change.id }
                if (index != -1) {
                    result[index] = result[index].copy(position = change.position)
                }
            }
            is NodeChange.Selection -> {
                val index = result.indexOfFirst { it.id == change.id }
                if (index != -1) {
                    result[index] = result[index].copy(selected = change.selected)
                }
            }
            is NodeChange.Remove -> {
                result.removeAll { it.id == change.id }
            }
            is NodeChange.Add -> {
                if (result.none { it.id == change.item.id }) {
                    result.add(change.item)
                }
            }
            is NodeChange.Dimensions -> {
                val index = result.indexOfFirst { it.id == change.id }
                if (index != -1) {
                    result[index] = result[index].copy(width = change.width, height = change.height)
                }
            }
            is NodeChange.Replace -> {
                val index = result.indexOfFirst { it.id == change.id }
                if (index != -1) {
                    result[index] = change.item
                }
            }
        }
    }
    return result
}

/**
 * Applies an array of edge changes to an edge list and returns the new list.
 *
 * This is the primary state-update function for edges. Use it in your
 * onEdgesChange callback:
 *
 * ```kotlin
 * onEdgesChange = { changes ->
 *     edges = applyEdgeChanges(changes, edges)
 * }
 * ```
 */
fun <E> applyEdgeChanges(changes: List<EdgeChange<E>>, edges: List<Edge<E>>): List<Edge<E>> {
    val result = edges.toMutableList()
    for (change in changes) {
        when (change) {
            is EdgeChange.Selection -> {
                val index = result.indexOfFirst { it.id == change.id }
                if (index != -1) {
                    result[index] = result[index].copy(selected = change.selected)
                }
            }
            is EdgeChange.Remove -> {
                result.removeAll { it.id == change.id }
            }
            is EdgeChange.Add -> {
                if (result.none { it.id == change.item.id }) {
                    result.add(change.item)
                }
            }
            is EdgeChange.Replace -> {
                val index = result.indexOfFirst { it.id == change.id }
                if (index != -1) {
                    result[index] = change.item
                }
            }
        }
    }
    return result
}
