package io.github.kotlinflow

import io.github.kotlinflow.models.EdgeType
import io.github.kotlinflow.models.MarkerType
import io.github.kotlinflow.types.BackgroundVariant
import io.github.kotlinflow.types.ColorMode
import io.github.kotlinflow.types.ConnectionMode
import io.github.kotlinflow.types.CoordinateExtent
import io.github.kotlinflow.types.HandleType
import io.github.kotlinflow.types.NodeOrigin
import io.github.kotlinflow.types.PanOnScrollMode
import io.github.kotlinflow.types.Position
import io.github.kotlinflow.types.ResizeDirection
import io.github.kotlinflow.types.SelectionMode
import io.github.kotlinflow.types.ZIndexMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TypesTests {

    @Test
    fun enumsValues() {
        assertEquals(Position.TOP, Position.valueOf("TOP"))
        assertEquals(HandleType.SOURCE, HandleType.valueOf("SOURCE"))
        assertEquals(SelectionMode.PARTIAL, SelectionMode.valueOf("PARTIAL"))
        assertEquals(ConnectionMode.STRICT, ConnectionMode.valueOf("STRICT"))
        assertEquals(ColorMode.SYSTEM, ColorMode.valueOf("SYSTEM"))
        assertEquals(ZIndexMode.AUTO, ZIndexMode.valueOf("AUTO"))
        assertEquals(PanOnScrollMode.FREE, PanOnScrollMode.valueOf("FREE"))
        assertEquals(BackgroundVariant.DOTS, BackgroundVariant.valueOf("DOTS"))
        assertEquals(ResizeDirection.BOTTOM_RIGHT, ResizeDirection.valueOf("BOTTOM_RIGHT"))
        assertEquals(MarkerType.ARROW, MarkerType.valueOf("ARROW"))
        assertEquals(EdgeType.DEFAULT, EdgeType.valueOf("DEFAULT"))
    }

    @Test
    fun nodeOriginDefaults() {
        assertEquals(0f, NodeOrigin.TopLeft.x, 0.001f)
        assertEquals(0f, NodeOrigin.TopLeft.y, 0.001f)
        assertEquals(0.5f, NodeOrigin.Center.x, 0.001f)
        assertEquals(0.5f, NodeOrigin.Center.y, 0.001f)
    }

    @Test
    fun coordinateExtentInfinite() {
        assertNotNull(CoordinateExtent.Infinite)
    }
}
