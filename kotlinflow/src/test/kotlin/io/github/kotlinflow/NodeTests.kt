package io.github.kotlinflow

import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.types.CoordinateExtent
import io.github.kotlinflow.types.NodeExtent
import io.github.kotlinflow.types.NodeOrigin
import io.github.kotlinflow.types.Position
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeTests {

    @Test
    fun initWithDefaults() {
        val node = Node(id = "1", position = XYPosition.Zero, data = "Hello")
        assertEquals("1", node.id)
        assertEquals(XYPosition.Zero, node.position)
        assertEquals("Hello", node.data)
        assertEquals("default", node.type)
        assertNull(node.parentId)
        assertFalse(node.selected)
        assertFalse(node.hidden)
        assertNull(node.width)
        assertNull(node.height)
        assertTrue(node.draggable)
        assertTrue(node.selectable)
        assertTrue(node.connectable)
        assertTrue(node.deletable)
        assertFalse(node.expandable)
        assertTrue(node.expanded)
        assertFalse(node.expandParent)
        assertTrue(node.focusable)
        assertEquals(0, node.zIndex)
        assertEquals(NodeOrigin.TopLeft, node.origin)
        assertNull(node.sourcePosition)
        assertNull(node.targetPosition)
        assertNull(node.extent)
        assertNull(node.style)
    }

    @Test
    fun initWithCustomValues() {
        val node = Node(
            id = "n1",
            position = XYPosition(100f, 200f),
            data = 42,
            type = "custom",
            parentId = "parent",
            selected = true,
            hidden = true,
            width = 300f,
            height = 150f,
            draggable = false,
            selectable = false,
            connectable = false,
            deletable = false,
            expandable = true,
            expanded = false,
            expandParent = true,
            focusable = false,
            zIndex = 5,
            origin = NodeOrigin.Center,
            sourcePosition = Position.RIGHT,
            targetPosition = Position.LEFT,
            extent = NodeExtent.Parent
        )
        assertEquals("custom", node.type)
        assertEquals("parent", node.parentId)
        assertTrue(node.selected)
        assertTrue(node.hidden)
        assertEquals(300f, node.width)
        assertEquals(150f, node.height)
        assertFalse(node.draggable)
        assertTrue(node.expandable)
        assertFalse(node.expanded)
        assertEquals(NodeExtent.Parent, node.extent)
        assertEquals(Position.RIGHT, node.sourcePosition)
        assertEquals(Position.LEFT, node.targetPosition)
    }

    @Test
    fun extentParent() {
        val node = Node(id = "1", position = XYPosition.Zero, data = "x", extent = NodeExtent.Parent)
        assertEquals(NodeExtent.Parent, node.extent)
    }

    @Test
    fun extentCoordinate() {
        val ce = CoordinateExtent(minX = 0f, minY = 0f, maxX = 500f, maxY = 500f)
        val node = Node(id = "1", position = XYPosition.Zero, data = "x", extent = NodeExtent.Explicit(ce))
        val explicit = node.extent as? NodeExtent.Explicit
        assertEquals(500f, explicit?.coordinateExtent?.maxX)
    }

    @Test
    fun equatable() {
        val a = Node(id = "1", position = XYPosition.Zero, data = "A")
        val b = Node(id = "1", position = XYPosition.Zero, data = "A")
        val c = Node(id = "2", position = XYPosition.Zero, data = "A")
        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    @Test
    fun codableRoundTrip() {
        val json = Json { prettyPrint = true }
        val node = Node(
            id = "n1",
            position = XYPosition(10f, 20f),
            data = "test",
            type = "custom",
            selected = true,
            zIndex = 3
        )
        val encoded = json.encodeToString(node)
        val decoded = json.decodeFromString<Node<String>>(encoded)
        assertEquals("n1", decoded.id)
        assertEquals(XYPosition(10f, 20f), decoded.position)
        assertEquals("test", decoded.data)
        assertEquals("custom", decoded.type)
        assertTrue(decoded.selected)
        assertEquals(3, decoded.zIndex)
    }

    @Test
    fun hashable() {
        val a = Node(id = "1", position = XYPosition.Zero, data = "A")
        val b = Node(id = "1", position = XYPosition.Zero, data = "A")
        val set = hashSetOf(a, b)
        assertEquals(1, set.size)
    }

    @Test
    fun mutableCopy() {
        var node = Node(id = "1", position = XYPosition.Zero, data = "A")
        node = node.copy(position = XYPosition(100f, 200f), selected = true, hidden = true)
        assertEquals(100f, node.position.x)
        assertTrue(node.selected)
        assertTrue(node.hidden)
    }
}
