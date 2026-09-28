package io.github.kotlinflow.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.utils.KotlinFlowInstance

/**
 * CompositionLocal providing access to [KotlinFlowState] in overlay components.
 */
val LocalKotlinFlowState = compositionLocalOf<KotlinFlowState> {
    error("No KotlinFlowState provided. Make sure to call this within a KotlinFlow canvas.")
}

/**
 * CompositionLocal providing access to [KotlinFlowInstance] from deeply nested composables.
 */
val LocalKotlinFlowInstance = compositionLocalOf<KotlinFlowInstance?> { null }

/**
 * CompositionLocal indicating whether all nodes have been measured.
 */
val LocalNodesInitialized = compositionLocalOf { false }

/**
 * Composable wrapper providing a [KotlinFlowStore] to child composables (SwiftFlow alias).
 */
@Composable
fun <NodeData, EdgeData> SwiftFlowProvider(
    initialNodes: List<Node<NodeData>> = emptyList(),
    initialEdges: List<Edge<EdgeData>> = emptyList(),
    content: @Composable (KotlinFlowStore<NodeData, EdgeData>) -> Unit
) {
    KotlinFlowProvider(initialNodes, initialEdges, content)
}

/**
 * Composable wrapper providing a [KotlinFlowStore] to child composables.
 */
@Composable
fun <NodeData, EdgeData> KotlinFlowProvider(
    initialNodes: List<Node<NodeData>> = emptyList(),
    initialEdges: List<Edge<EdgeData>> = emptyList(),
    content: @Composable (KotlinFlowStore<NodeData, EdgeData>) -> Unit
) {
    val store = remember { KotlinFlowStore(initialNodes, initialEdges) }
    content(store)
}
