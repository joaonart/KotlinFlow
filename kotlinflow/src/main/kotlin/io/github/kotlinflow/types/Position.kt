package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * The position of a handle relative to its node.
 */
@Serializable
enum class Position {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
}
