package io.github.kotlinflow

import io.github.kotlinflow.models.Connection
import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EdgeType
import io.github.kotlinflow.models.EmptyEdgeData
import io.github.kotlinflow.types.Position
import io.github.kotlinflow.utils.addEdge
import io.github.kotlinflow.utils.getBezierPath
import io.github.kotlinflow.utils.getEdgeAngleAtEnd
import io.github.kotlinflow.utils.getEdgeAngleAtStart
import io.github.kotlinflow.utils.getEdgeMidpoint
import io.github.kotlinflow.utils.getEdgePathResult
import io.github.kotlinflow.utils.getSimpleBezierPath
import io.github.kotlinflow.utils.getSmoothStepPath
import io.github.kotlinflow.utils.getStepPath
import io.github.kotlinflow.utils.getStraightPath
import io.github.kotlinflow.utils.reconnectEdge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class EdgeUtilsTests {

    @Test
    fun addEdgeCreatesNewEdge() {
        val edges = emptyList<Edge<EmptyEdgeData>>()
        val conn = Connection(source = "1", target = "2")
        val result = addEdge(conn, edges)
        assertEquals(1, result.size)
        assertEquals("1", result[0].source)
        assertEquals("2", result[0].target)
    }

    @Test
    fun addEdgePreventsDuplicates() {
        val edges = listOf(Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2"))
        val conn = Connection(source = "1", target = "2")
        val result = addEdge(conn, edges)
        assertEquals(1, result.size)
    }

    @Test
    fun addEdgeAllowsDifferentHandlesBetweenSameNodes() {
        val existing = Edge<EmptyEdgeData>(
            id = "e1", source = "1", target = "2",
            sourceHandle = "a", targetHandle = null
        )
        val edges = listOf(existing)
        val conn = Connection(source = "1", target = "2", sourceHandle = "b", targetHandle = null)
        val result = addEdge(conn, edges)
        assertEquals(2, result.size)
        assertEquals("b", result[1].sourceHandle)
    }

    @Test
    fun addEdgePreventsDuplicateWhenHandlesMatch() {
        val existing = Edge<EmptyEdgeData>(
            id = "e1", source = "1", target = "2",
            sourceHandle = "a", targetHandle = "x"
        )
        val edges = listOf(existing)
        val conn = Connection(source = "1", target = "2", sourceHandle = "a", targetHandle = "x")
        val result = addEdge(conn, edges)
        assertEquals(1, result.size)
    }

    @Test
    fun reconnectEdgeUpdatesConnection() {
        val existing = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")
        val newConn = Connection(source = "1", target = "3")
        val result = reconnectEdge(existing, newConn, listOf(existing))
        assertEquals(1, result.size)
        assertEquals("3", result[0].target)
    }

    @Test
    fun pathGenerators() {
        assertNotNull(getBezierPath(0f, 0f, 100f, 100f))
        assertNotNull(getSimpleBezierPath(0f, 0f, 100f, 100f))
        assertNotNull(getStraightPath(0f, 0f, 100f, 100f))
        assertNotNull(getStepPath(0f, 0f, 100f, 100f))
        assertNotNull(getSmoothStepPath(0f, 0f, 100f, 100f))
    }

    @Test
    fun positionAwarePathGenerators() {
        assertNotNull(getBezierPath(0f, 0f, Position.RIGHT, 100f, 100f, Position.LEFT))
        assertNotNull(getSimpleBezierPath(0f, 0f, Position.RIGHT, 100f, 100f, Position.LEFT))
        assertNotNull(getStepPath(0f, 0f, Position.RIGHT, 100f, 100f, Position.LEFT))
        assertNotNull(getSmoothStepPath(0f, 0f, Position.RIGHT, 100f, 100f, Position.LEFT))
    }

    @Test
    fun edgeMidpointAndPathResult() {
        val mid = getEdgeMidpoint(EdgeType.STRAIGHT, 0f, 0f, 100f, 100f)
        assertEquals(50f, mid.x, 0.001f)
        assertEquals(50f, mid.y, 0.001f)

        val result = getEdgePathResult(EdgeType.STRAIGHT, 0f, 0f, 100f, 100f)
        assertEquals(50f, result.labelX, 0.001f)
        assertEquals(50f, result.labelY, 0.001f)
    }

    @Test
    fun edgeAngles() {
        val angleEnd = getEdgeAngleAtEnd(EdgeType.STRAIGHT, 0f, 0f, 100f, 0f)
        assertEquals(0f, angleEnd, 0.001f)

        val angleStart = getEdgeAngleAtStart(EdgeType.STRAIGHT, 0f, 0f, 100f, 0f)
        assertEquals(0f, angleStart, 0.001f)
    }
}
