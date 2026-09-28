package io.github.kotlinflow.types

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.Node

/**
 * The result of an `onBeforeDelete` callback, allowing the deletion to be
 * cancelled or modified before it takes effect.
 */
sealed interface BeforeDeleteResult<out NodeData, out EdgeData> {
    data object Cancel : BeforeDeleteResult<Nothing, Nothing>
    data class Delete<NodeData, EdgeData>(
        val nodes: List<Node<NodeData>>,
        val edges: List<Edge<EdgeData>>
    ) : BeforeDeleteResult<NodeData, EdgeData>
}
