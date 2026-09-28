package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Describes the extent constraint for a node's draggable area.
 * - [Parent]: Constrains the node within its parent's bounds.
 * - [Explicit]: Constrains the node within explicit coordinates.
 */
@Serializable
sealed interface NodeExtent {
    @Serializable
    data object Parent : NodeExtent

    @Serializable
    data class Explicit(val coordinateExtent: CoordinateExtent) : NodeExtent

    companion object {
        fun of(coordinateExtent: CoordinateExtent): NodeExtent = Explicit(coordinateExtent)
    }
}
