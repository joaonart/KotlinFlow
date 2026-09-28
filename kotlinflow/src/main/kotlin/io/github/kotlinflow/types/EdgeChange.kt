package io.github.kotlinflow.types

import io.github.kotlinflow.models.Edge

/**
 * Describes a mutation to an edge in the graph.
 * Apply changes via [io.github.kotlinflow.utils.applyEdgeChanges].
 */
sealed interface EdgeChange<out EdgeData> {
    data class Selection(val id: String, val selected: Boolean) : EdgeChange<Nothing>
    data class Remove(val id: String) : EdgeChange<Nothing>
    data class Add<EdgeData>(val item: Edge<EdgeData>) : EdgeChange<EdgeData>
    data class Replace<EdgeData>(val id: String, val item: Edge<EdgeData>) : EdgeChange<EdgeData>
}
