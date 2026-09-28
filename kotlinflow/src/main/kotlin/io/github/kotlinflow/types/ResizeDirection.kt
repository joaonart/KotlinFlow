package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * The direction of a resize handle placement.
 */
@Serializable
enum class ResizeDirection {
    TOP_LEFT,
    TOP,
    TOP_RIGHT,
    LEFT,
    RIGHT,
    BOTTOM_LEFT,
    BOTTOM,
    BOTTOM_RIGHT
}
