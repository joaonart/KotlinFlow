package io.github.kotlinflow.models

import kotlinx.serialization.Serializable
import kotlin.math.round

/**
 * A 2D coordinate representing a position on the canvas.
 */
@Serializable
data class XYPosition(
    val x: Float,
    val y: Float
) {
    operator fun plus(other: XYPosition): XYPosition = XYPosition(x + other.x, y + other.y)
    operator fun minus(other: XYPosition): XYPosition = XYPosition(x - other.x, y - other.y)

    /**
     * Returns a new position snapped to the given grid (gridX, gridY).
     */
    fun snapped(gridX: Float, gridY: Float): XYPosition {
        val gx = if (gridX > 0f) gridX else 1f
        val gy = if (gridY > 0f) gridY else 1f
        return XYPosition(
            x = round(x / gx) * gx,
            y = round(y / gy) * gy
        )
    }

    companion object {
        val Zero = XYPosition(0f, 0f)
    }
}
