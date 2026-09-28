package io.github.kotlinflow

import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.utils.getIntersectingNodes
import io.github.kotlinflow.utils.isNodeIntersecting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeIntersectionTests {

    @Test
    fun nodeIntersectionDetection() {
        val n1 = Node(id = "1", position = XYPosition(0f, 0f), data = "1", width = 100f, height = 100f)
        val n2 = Node(id = "2", position = XYPosition(50f, 50f), data = "2", width = 100f, height = 100f)
        val n3 = Node(id = "3", position = XYPosition(500f, 500f), data = "3", width = 100f, height = 100f)

        assertTrue(isNodeIntersecting(n1, n2))
        assertFalse(isNodeIntersecting(n1, n3))

        val intersecting = getIntersectingNodes(n1, listOf(n1, n2, n3))
        assertEquals(1, intersecting.size)
        assertEquals("2", intersecting[0].id)
    }
}
