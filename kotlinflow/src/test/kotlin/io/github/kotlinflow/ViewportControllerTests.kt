package io.github.kotlinflow

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.kotlinflow.models.Viewport
import io.github.kotlinflow.utils.KotlinFlowInstance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ViewportControllerTests {

    @Test
    fun zoomAndCenterOperations() {
        val instance = KotlinFlowInstance()
        instance.viewSize = Size(1000f, 800f)

        instance.setCenter(500f, 400f, zoom = 1f, animated = false)
        assertEquals(1f, instance.viewport.zoom, 0.001f)

        instance.zoomIn(animated = false)
        assertTrue(instance.viewport.zoom > 1f)

        instance.zoomOut(animated = false)
        assertEquals(1f, instance.viewport.zoom, 0.05f)

        instance.reset(animated = false)
        assertEquals(Viewport.Identity, instance.viewport)
    }

    @Test
    fun coordinateConversions() {
        val instance = KotlinFlowInstance()
        instance.setViewport(Viewport(x = 100f, y = 50f, zoom = 2f), animated = false)

        val screenPt = Offset(300f, 250f)
        val flowPt = instance.screenToFlowPosition(screenPt)
        assertEquals(100f, flowPt.x, 0.001f)
        assertEquals(100f, flowPt.y, 0.001f)

        val backToScreen = instance.flowToScreenPosition(flowPt)
        assertEquals(screenPt.x, backToScreen.x, 0.001f)
        assertEquals(screenPt.y, backToScreen.y, 0.001f)
    }
}
