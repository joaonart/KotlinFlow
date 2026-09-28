package io.github.kotlinflow

import io.github.kotlinflow.models.Viewport
import org.junit.Assert.assertEquals
import org.junit.Test

class ViewportTests {

    @Test
    fun identity() {
        assertEquals(0f, Viewport.Identity.x, 0.001f)
        assertEquals(0f, Viewport.Identity.y, 0.001f)
        assertEquals(1f, Viewport.Identity.zoom, 0.001f)
    }

    @Test
    fun clampedZoom() {
        val low = Viewport(0f, 0f, 0.05f)
        assertEquals(Viewport.MinZoom, low.clampedZoom, 0.001f)

        val high = Viewport(0f, 0f, 10f)
        assertEquals(Viewport.MaxZoom, high.clampedZoom, 0.001f)

        val normal = Viewport(0f, 0f, 1.5f)
        assertEquals(1.5f, normal.clampedZoom, 0.001f)
    }
}
