package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Controls how the selection box determines which nodes are selected.
 * - [PARTIAL]: Nodes are selected if the selection box partially overlaps them.
 * - [FULL]: Nodes are selected only if fully contained within the selection box.
 */
@Serializable
enum class SelectionMode {
    PARTIAL,
    FULL
}
