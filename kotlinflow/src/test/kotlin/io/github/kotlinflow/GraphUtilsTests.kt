package io.github.kotlinflow

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EmptyEdgeData
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.utils.elementsToDelete
import io.github.kotlinflow.utils.getConnectedEdges
import io.github.kotlinflow.utils.getIncomers
import io.github.kotlinflow.utils.getOutgoers
import org.junit.Assert.assertEquals
import org.junit.Test

class GraphUtilsTests {

    @Test
    fun getIncomersAndOutgoers() {
        val n1 = Node(id = "1", position = XYPosition.Zero, data = "1")
        val n2 = Node(id = "2", position = XYPosition.Zero, data = "2")
        val n3 = Node(id = "3", position = XYPosition.Zero, data = "3")
        val nodes = listOf(n1, n2, n3)

        val e1 = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")
        val e2 = Edge<EmptyEdgeData>(id = "e2", source = "2", target = "3")
        val edges = listOf(e1, e2)

        val incomers = getIncomers(n2, nodes, edges)
        assertEquals(1, incomers.size)
        assertEquals("1", incomers[0].id)

        val outgoers = getOutgoers(n2, nodes, edges)
        assertEquals(1, outgoers.size)
        assertEquals("3", outgoers[0].id)
    }

    @Test
    fun getConnectedEdgesSingleAndMultiple() {
        val n1 = Node(id = "1", position = XYPosition.Zero, data = "1")
        val n2 = Node(id = "2", position = XYPosition.Zero, data = "2")
        val e1 = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")
        val edges = listOf(e1)

        val connSingle = getConnectedEdges(n1, edges)
        assertEquals(1, connSingle.size)

        val connMulti = getConnectedEdges(listOf(n1, n2), edges)
        assertEquals(1, connMulti.size)
    }

    @Test
    fun elementsToDeleteResolvesCascade() {
        val n1 = Node(id = "1", position = XYPosition.Zero, data = "1", deletable = true)
        val n2 = Node(id = "2", position = XYPosition.Zero, data = "2", deletable = true)
        val e1 = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2", deletable = true)

        val result = elementsToDelete(listOf(n1, n2), listOf(e1), nodeIds = listOf("1"), edgeIds = emptyList())
        assertEquals(1, result.nodes.size)
        assertEquals("1", result.nodes[0].id)
        // Incident edge should also be deleted
        assertEquals(1, result.edges.size)
        assertEquals("e1", result.edges[0].id)
    }
}
