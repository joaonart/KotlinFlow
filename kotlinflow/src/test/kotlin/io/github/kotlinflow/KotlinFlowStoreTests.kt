package io.github.kotlinflow

import io.github.kotlinflow.models.Connection
import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EmptyEdgeData
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.state.KotlinFlowStore
import io.github.kotlinflow.types.EdgeChange
import io.github.kotlinflow.types.HandleType
import io.github.kotlinflow.types.NodeChange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KotlinFlowStoreTests {

    @Test
    fun storeStateOperations() {
        val n1 = Node(id = "1", position = XYPosition.Zero, data = "A")
        val n2 = Node(id = "2", position = XYPosition.Zero, data = "B")
        val e1 = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")

        val store = KotlinFlowStore(listOf(n1, n2), listOf(e1))

        assertEquals(2, store.nodes.size)
        assertEquals(1, store.edges.size)
        assertNotNull(store.getNode("1"))
        assertNotNull(store.getEdge("e1"))
        assertNull(store.getNode("999"))

        // onNodesChange
        store.onNodesChange(listOf(NodeChange.Selection("1", true)))
        assertTrue(store.selectedNodeIds.contains("1"))

        // onConnect
        store.onConnect(Connection("2", "1"))
        assertEquals(2, store.edges.size)

        // getNodesData
        val data = store.getNodesData(listOf("1", "2"))
        assertEquals(2, data.size)

        // getNodeConnections
        val conns = store.getNodeConnections("1", HandleType.SOURCE)
        assertEquals(1, conns.size)

        // deleteElements
        store.deleteElements(nodeIds = listOf("2"))
        assertEquals(1, store.nodes.size)
        // Associated edges should also be removed
        assertEquals(0, store.edges.size)
    }
}
