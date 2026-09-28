package io.github.kotlinflow.types

import io.github.kotlinflow.models.XYPosition
import kotlinx.serialization.Serializable

/**
 * Defines boundary constraints for node dragging.
 * Nodes cannot be dragged outside these bounds.
 * Use [CoordinateExtent.Infinite] for no constraints (default).
 */
@Serializable
data class CoordinateExtent(
    val minX: Float,
    val minY: Float,
    val maxX: Float,
    val maxY: Float
) {
    /**
     * Clamps a position to within the extent bounds.
     */
    fun clamp(position: XYPosition): XYPosition {
        return XYPosition(
            x = position.x.coerceIn(minX, maxX),
            y = position.y.coerceIn(minY, maxY)
        )
    }

    companion object {
        val Infinite = CoordinateExtent(
            minX = Float.NEGATIVE_INFINITY,
            minY = Float.NEGATIVE_INFINITY,
            maxX = Float.POSITIVE_INFINITY,
            maxY = Float.POSITIVE_INFINITY
        )

        /**
         * Creates a CoordinateExtent from a pair of coordinate arrays: [[minX, minY], [maxX, maxY]].
         */
        operator fun invoke(extent: List<List<Float>>): CoordinateExtent {
            require(extent.size == 2 && extent[0].size == 2 && extent[1].size == 2) {
                "CoordinateExtent requires [[minX, minY], [maxX, maxY]]"
            }
            return CoordinateExtent(
                minX = extent[0][0],
                minY = extent[0][1],
                maxX = extent[1][0],
                maxY = extent[1][1]
            )
        }
    }
}
