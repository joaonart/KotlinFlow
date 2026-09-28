package io.github.kotlinflow

import androidx.compose.ui.geometry.Size
import io.github.kotlinflow.models.Node
import io.github.kotlinflow.models.Viewport
import io.github.kotlinflow.models.XYPosition
import io.github.kotlinflow.state.KotlinFlowState
import io.github.kotlinflow.types.AnyNodeSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KotlinFlowStateTests {

    @Test
    fun viewportMutations() {
        val state = KotlinFlowState()
        state.viewSize = Size(1000f, 800f)

        state.setViewport(Viewport(100f, 100f, 1f))
        assertEquals(100f, state.viewport.x, 0.001f)

        state.zoomIn(animated = false)
        assertTrue(state.viewport.zoom > 1f)

        state.zoomOut(animated = false)
        assertEquals(1f, state.viewport.zoom, 0.05f)

        state.zoomTo(2f, animated = false)
        assertEquals(2f, state.viewport.zoom, 0.001f)
    }

    @Test
    fun fitViewCentersContent() {
        val state = KotlinFlowState()
        state.viewSize = Size(1000f, 800f)
        val n1 = Node(id = "1", position = XYPosition(0f, 0f), data = "1")
        val n2 = Node(id = "2", position = XYPosition(400f, 300f), data = "2")
        state.nodes = listOf(AnyNodeSnapshot.from(n1), AnyNodeSnapshot.from(n2))

        state.fitView(padding = 50f, animated = false)
        assertTrue(state.viewport.zoom > 0f)
    }
}
