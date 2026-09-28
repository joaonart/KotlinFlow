package io.github.kotlinflow.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.kotlinflow.models.Connection
import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.Viewport
import io.github.kotlinflow.types.DefaultEdgeOptions
import io.github.kotlinflow.types.EdgeChange
import io.github.kotlinflow.types.HandleType
import io.github.kotlinflow.types.NodeChange
import io.github.kotlinflow.utils.ElementsToDeleteResult
import io.github.kotlinflow.utils.addEdge
import io.github.kotlinflow.utils.applyEdgeChanges
import io.github.kotlinflow.utils.applyNodeChanges
import io.github.kotlinflow.utils.elementsToDelete

typealias SwiftFlowStore<NodeData, EdgeData> = KotlinFlowStore<NodeData, EdgeData>

/**
 * Observable store managing the complete state of a KotlinFlow graph.
 *
 * Use [KotlinFlowStore] for centralized state management,
 * similar to the React Flow / SwiftFlow store pattern.
 */
class KotlinFlowStore<NodeData, EdgeData>(
    initialNodes: List<Node<NodeData>> = emptyList(),
    initialEdges: List<Edge<EdgeData>> = emptyList()
) {
    var nodes: List<Node<NodeData>> by mutableStateOf(initialNodes)
        private set

    var edges: List<Edge<EdgeData>> by mutableStateOf(initialEdges)
        private set

    var viewport: Viewport by mutableStateOf(Viewport.Identity)

    var selectedNodeIds: Set<String> by mutableStateOf(
        initialNodes.filter { it.selected }.map { it.id }.toSet()
    )
        private set

    var selectedEdgeIds: Set<String> by mutableStateOf(
        initialEdges.filter { it.selected }.map { it.id }.toSet()
    )
        private set

    /** Applies a list of node changes to the current nodes. */
    fun onNodesChange(changes: List<NodeChange<NodeData>>) {
        nodes = applyNodeChanges(changes, nodes)
        selectedNodeIds = nodes.filter { it.selected }.map { it.id }.toSet()
    }

    /** Applies a list of edge changes to the current edges. */
    fun onEdgesChange(changes: List<EdgeChange<EdgeData>>) {
        edges = applyEdgeChanges(changes, edges)
        selectedEdgeIds = edges.filter { it.selected }.map { it.id }.toSet()
    }

    /** Creates a new edge from a connection. */
    fun onConnect(connection: Connection, defaults: DefaultEdgeOptions? = null) {
        edges = addEdge(connection, edges, defaults)
    }

    /** Returns a specific node by ID. */
    fun getNode(id: String): Node<NodeData>? {
        return nodes.firstOrNull { it.id == id }
    }

    /** Returns a specific edge by ID. */
    fun getEdge(id: String): Edge<EdgeData>? {
        return edges.firstOrNull { it.id == id }
    }

    /** Returns data for the specified node IDs. */
    fun getNodesData(ids: List<String>): List<Pair<String, NodeData>> {
        val idSet = ids.toSet()
        return nodes.filter { it.id in idSet }.map { it.id to it.data }
    }

    /** Returns connections for a specific node, optionally filtered by handle type. */
    fun getNodeConnections(
        nodeId: String,
        handleType: HandleType? = null
    ): List<Connection> {
        return edges.mapNotNull { edge ->
            when {
                handleType == HandleType.SOURCE && edge.source == nodeId -> {
                    Connection(edge.source, edge.target, edge.sourceHandle, edge.targetHandle)
                }
                handleType == HandleType.TARGET && edge.target == nodeId -> {
                    Connection(edge.source, edge.target, edge.sourceHandle, edge.targetHandle)
                }
                handleType == null && (edge.source == nodeId || edge.target == nodeId) -> {
                    Connection(edge.source, edge.target, edge.sourceHandle, edge.targetHandle)
                }
                else -> null
            }
        }
    }

    /** Whether all nodes have been measured (have known sizes). */
    val nodesInitialized: Boolean
        get() = nodes.isNotEmpty() && nodes.all { it.width != null && it.height != null }

    /** Deletes deletable nodes and edges by ID, returning the deleted elements. */
    fun deleteElements(
        nodeIds: List<String> = emptyList(),
        edgeIds: List<String> = emptyList()
    ): ElementsToDeleteResult<NodeData, EdgeData> {
        val result = elementsToDelete(nodes, edges, nodeIds, edgeIds)
        onNodesChange(result.nodes.map { NodeChange.Remove(it.id) })
        onEdgesChange(result.edges.map { EdgeChange.Remove(it.id) })
        return result
    }
}
