package io.github.kotlinflow

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EmptyEdgeData
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.types.EdgeChange
import io.github.kotlinflow.types.NodeChange
import io.github.kotlinflow.utils.applyEdgeChanges
import io.github.kotlinflow.utils.applyNodeChanges
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChangeUtilsTests {

    @Test
    fun applyNodePositionChange() {
        val nodes = listOf(Node(id = "1", position = XYPosition.Zero, data = "A"))
        val changes = listOf(NodeChange.Position(id = "1", position = XYPosition(100f, 200f)))
        val result = applyNodeChanges(changes, nodes)
        assertEquals(XYPosition(100f, 200f), result[0].position)
    }

    @Test
    fun applyNodeSelectionChange() {
        val nodes = listOf(Node(id = "1", position = XYPosition.Zero, data = "A"))
        val changes = listOf(NodeChange.Selection(id = "1", selected = true))
        val result = applyNodeChanges(changes, nodes)
        assertTrue(result[0].selected)
    }

    @Test
    fun applyNodeRemoveChange() {
        val nodes = listOf(Node(id = "1", position = XYPosition.Zero, data = "A"))
        val changes = listOf(NodeChange.Remove(id = "1"))
        val result = applyNodeChanges(changes, nodes)
        assertTrue(result.isEmpty())
    }

    @Test
    fun applyNodeAddChange() {
        val nodes = listOf(Node(id = "1", position = XYPosition.Zero, data = "A"))
        val newNode = Node(id = "2", position = XYPosition(50f, 50f), data = "B")
        val changes = listOf(NodeChange.Add(newNode))
        val result = applyNodeChanges(changes, nodes)
        assertEquals(2, result.size)
        assertEquals("2", result[1].id)
    }

    @Test
    fun applyNodeDimensionsChange() {
        val nodes = listOf(Node(id = "1", position = XYPosition.Zero, data = "A"))
        val changes = listOf(NodeChange.Dimensions(id = "1", width = 120f, height = 80f))
        val result = applyNodeChanges(changes, nodes)
        assertEquals(120f, result[0].width)
        assertEquals(80f, result[0].height)
    }

    @Test
    fun applyNodeReplaceChange() {
        val nodes = listOf(Node(id = "1", position = XYPosition.Zero, data = "Old"))
        val replacement = Node(id = "1", position = XYPosition(10f, 10f), data = "New")
        val changes = listOf(NodeChange.Replace(id = "1", item = replacement))
        val result = applyNodeChanges(changes, nodes)
        assertEquals("New", result[0].data)
        assertEquals(XYPosition(10f, 10f), result[0].position)
    }

    @Test
    fun applyEdgeSelectionChange() {
        val edges = listOf(Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2"))
        val changes = listOf(EdgeChange.Selection(id = "e1", selected = true))
        val result = applyEdgeChanges(changes, edges)
        assertTrue(result[0].selected)
    }

    @Test
    fun applyEdgeRemoveChange() {
        val edges = listOf(Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2"))
        val changes = listOf(EdgeChange.Remove(id = "e1"))
        val result = applyEdgeChanges(changes, edges)
        assertTrue(result.isEmpty())
    }

    @Test
    fun applyEdgeAddChange() {
        val edges = listOf(Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2"))
        val newEdge = Edge<EmptyEdgeData>(id = "e2", source = "2", target = "3")
        val changes = listOf(EdgeChange.Add(newEdge))
        val result = applyEdgeChanges(changes, edges)
        assertEquals(2, result.size)
    }
}
