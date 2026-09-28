package io.github.kotlinflow

import io.github.kotlinflow.models.XYPosition
import org.junit.Assert.assertEquals
import org.junit.Test

class XYPositionTests {

    @Test
    fun zero() {
        assertEquals(0f, XYPosition.Zero.x, 0.001f)
        assertEquals(0f, XYPosition.Zero.y, 0.001f)
    }

    @Test
    fun addition() {
        val a = XYPosition(10f, 20f)
        val b = XYPosition(5f, 15f)
        val result = a + b
        assertEquals(15f, result.x, 0.001f)
        assertEquals(35f, result.y, 0.001f)
    }

    @Test
    fun subtraction() {
        val a = XYPosition(10f, 20f)
        val b = XYPosition(5f, 15f)
        val result = a - b
        assertEquals(5f, result.x, 0.001f)
        assertEquals(5f, result.y, 0.001f)
    }

    @Test
    fun snapping() {
        val pos = XYPosition(23f, 37f)
        val snapped = pos.snapped(20f, 20f)
        assertEquals(20f, snapped.x, 0.001f)
        assertEquals(40f, snapped.y, 0.001f)
    }
}
