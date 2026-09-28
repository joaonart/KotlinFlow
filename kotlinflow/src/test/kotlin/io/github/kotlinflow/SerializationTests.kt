package io.github.kotlinflow

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EmptyEdgeData
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.Viewport
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.utils.fromJSONString
import io.github.kotlinflow.utils.toJSONString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SerializationTests {

    @Test
    fun jsonRoundTrip() {
        val n1 = Node(id = "1", position = XYPosition(10f, 20f), data = "Input")
        val n2 = Node(id = "2", position = XYPosition(100f, 200f), data = "Output")
        val e1 = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")
        val vp = Viewport(x = 10f, y = 20f, zoom = 1.2f)

        val jsonString = toJSONString(listOf(n1, n2), listOf(e1), vp)
        assertNotNull(jsonString)

        val doc = fromJSONString<String, EmptyEdgeData>(jsonString)
        assertEquals(2, doc.nodes.size)
        assertEquals(1, doc.edges.size)
        assertEquals("1", doc.nodes[0].id)
        assertEquals("Input", doc.nodes[0].data)
        assertEquals("e1", doc.edges[0].id)
        assertEquals(1.2f, doc.viewport?.zoom ?: 0f, 0.001f)
    }
}
