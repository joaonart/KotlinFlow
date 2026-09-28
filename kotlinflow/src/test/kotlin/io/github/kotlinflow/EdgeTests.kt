package io.github.kotlinflow

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EdgeMarker
import io.github.kotlinflow.models.EdgeType
import io.github.kotlinflow.models.EmptyEdgeData
import io.github.kotlinflow.models.MarkerType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EdgeTests {

    @Test
    fun initWithDefaults() {
        val edge = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")
        assertEquals("e1", edge.id)
        assertEquals("1", edge.source)
        assertEquals("2", edge.target)
        assertNull(edge.sourceHandle)
        assertNull(edge.targetHandle)
        assertEquals(EdgeType.DEFAULT, edge.type)
        assertFalse(edge.selected)
        assertFalse(edge.hidden)
        assertNull(edge.label)
        assertFalse(edge.animated)
        assertNull(edge.markerStart)
        assertNull(edge.markerEnd)
        assertEquals(0, edge.zIndex)
        assertFalse(edge.reconnectable)
        assertTrue(edge.deletable)
        assertTrue(edge.focusable)
        assertEquals(20f, edge.interactionWidth)
        assertNull(edge.data)
        assertNull(edge.style)
    }

    @Test
    fun initWithCustomValues() {
        val edge = Edge<EmptyEdgeData>(
            id = "e1",
            source = "1",
            target = "2",
            sourceHandle = "out",
            targetHandle = "in",
            type = EdgeType.SMOOTHSTEP,
            selected = true,
            hidden = true,
            label = "Flow",
            animated = true,
            markerStart = EdgeMarker.Arrow,
            markerEnd = EdgeMarker.ArrowClosed,
            zIndex = 5,
            reconnectable = true,
            deletable = false,
            focusable = false,
            interactionWidth = 30f,
            data = EmptyEdgeData()
        )
        assertEquals("out", edge.sourceHandle)
        assertEquals("in", edge.targetHandle)
        assertEquals(EdgeType.SMOOTHSTEP, edge.type)
        assertTrue(edge.selected)
        assertEquals("Flow", edge.label)
        assertTrue(edge.animated)
        assertTrue(edge.reconnectable)
        assertFalse(edge.deletable)
        assertEquals(30f, edge.interactionWidth)
    }

    @Test
    fun codableRoundTrip() {
        val json = Json { prettyPrint = true }
        val edge = Edge<EmptyEdgeData>(
            id = "e1",
            source = "1",
            target = "2",
            type = EdgeType.SMOOTHSTEP,
            label = "test",
            reconnectable = true
        )
        val encoded = json.encodeToString(Edge.serializer(EmptyEdgeData.serializer()), edge)
        val decoded = json.decodeFromString(Edge.serializer(EmptyEdgeData.serializer()), encoded)
        assertEquals("e1", decoded.id)
        assertEquals("1", decoded.source)
        assertEquals("2", decoded.target)
        assertEquals(EdgeType.SMOOTHSTEP, decoded.type)
        assertEquals("test", decoded.label)
        assertTrue(decoded.reconnectable)
    }

    @Test
    fun equatable() {
        val a = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")
        val b = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")
        val c = Edge<EmptyEdgeData>(id = "e2", source = "1", target = "2")
        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    @Test
    fun hashable() {
        val a = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")
        val b = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")
        val set = hashSetOf(a, b)
        assertEquals(1, set.size)
    }

    @Test
    fun edgeMarkerDefaults() {
        val arrow = EdgeMarker.Arrow
        assertEquals(MarkerType.ARROW, arrow.type)
        assertEquals(12f, arrow.width)
        assertEquals(12f, arrow.height)

        val closed = EdgeMarker.ArrowClosed
        assertEquals(MarkerType.ARROW_CLOSED, closed.type)
    }

    @Serializable
    data class FlowData(val weight: Double)

    @Test
    fun edgeWithCustomData() {
        val edge = Edge(id = "e1", source = "1", target = "2", data = FlowData(weight = 1.5))
        assertEquals(1.5, edge.data?.weight ?: 0.0, 0.001)
    }
}
