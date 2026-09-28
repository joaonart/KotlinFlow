package io.github.kotlinflow

import io.github.kotlinflow.models.Edge
import io.github.kotlinflow.models.EmptyEdgeData
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.utils.LayoutAlgorithm
import io.github.kotlinflow.utils.LayoutDirection
import io.github.kotlinflow.utils.computeAutoLayout
import org.junit.Assert.assertEquals
import org.junit.Test

class AutoLayoutTests {

    @Test
    fun treeLayoutComputesPositions() {
        val n1 = Node(id = "1", position = XYPosition.Zero, data = "1")
        val n2 = Node(id = "2", position = XYPosition.Zero, data = "2")
        val e1 = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")

        val changes = computeAutoLayout(
            nodes = listOf(n1, n2),
            edges = listOf(e1),
            algorithm = LayoutAlgorithm.Tree(direction = LayoutDirection.TOP_TO_BOTTOM)
        )

        assertEquals(2, changes.size)
    }

    @Test
    fun gridLayoutComputesPositions() {
        val nodes = (1..6).map { Node(id = "$it", position = XYPosition.Zero, data = "$it") }
        val changes = computeAutoLayout(
            nodes = nodes,
            edges = emptyList<Edge<EmptyEdgeData>>(),
            algorithm = LayoutAlgorithm.Grid(columns = 3, nodeSpacing = 40f)
        )

        assertEquals(6, changes.size)
    }

    @Test
    fun forceDirectedLayoutComputesPositions() {
        val n1 = Node(id = "1", position = XYPosition(0f, 0f), data = "1")
        val n2 = Node(id = "2", position = XYPosition(10f, 10f), data = "2")
        val e1 = Edge<EmptyEdgeData>(id = "e1", source = "1", target = "2")

        val changes = computeAutoLayout(
            nodes = listOf(n1, n2),
            edges = listOf(e1),
            algorithm = LayoutAlgorithm.ForceDirected(iterations = 10)
        )

        assertEquals(2, changes.size)
    }
}
