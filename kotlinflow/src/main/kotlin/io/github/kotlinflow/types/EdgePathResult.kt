package io.github.kotlinflow.types

import androidx.compose.ui.graphics.Path

/**
 * Result of an edge path computation, including the Path and label positioning info.
 */
data class EdgePathResult(
    /** The renderable path for the edge. */
    val path: Path,
    /** X coordinate for label placement (midpoint of the path). */
    val labelX: Float,
    /** Y coordinate for label placement (midpoint of the path). */
    val labelY: Float,
    /** Source endpoint X coordinate. */
    val sourceX: Float,
    /** Source endpoint Y coordinate. */
    val sourceY: Float,
    /** Target endpoint X coordinate. */
    val targetX: Float,
    /** Target endpoint Y coordinate. */
    val targetY: Float
)
