package io.github.kotlinflow

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.utils.getNodesBounds
import io.github.kotlinflow.utils.getViewportForBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeometryUtilsTests {

    @Test
    fun getNodesBoundsCalculatesEnclosingRect() {
        val n1 = Node(id = "1", position = XYPosition(0f, 0f), data = "1", width = 100f, height = 50f)
        val n2 = Node(id = "2", position = XYPosition(200f, 300f), data = "2", width = 100f, height = 50f)
        val bounds = getNodesBounds(listOf(n1, n2), emptyMap())

        assertEquals(0f, bounds.left, 0.001f)
        assertEquals(0f, bounds.top, 0.001f)
        assertEquals(300f, bounds.right, 0.001f)
        assertEquals(350f, bounds.bottom, 0.001f)
    }

    @Test
    fun getViewportForBoundsComputesFittingZoom() {
        val bounds = Rect(0f, 0f, 400f, 200f)
        val viewportSize = Size(800f, 600f)
        val vp = getViewportForBounds(bounds, viewportSize, padding = 0f)

        assertTrue(vp.zoom > 0f)
    }
}
