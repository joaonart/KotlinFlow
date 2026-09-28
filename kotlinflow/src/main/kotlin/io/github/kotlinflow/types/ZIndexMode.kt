package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Controls how z-index values are managed for nodes and edges.
 * - [AUTO]: Automatically adjusts z-index for selections and sub-flows.
 * - [BASIC]: Only manages z-index for selections (selected nodes come to front).
 * - [MANUAL]: No automatic z-indexing; all values are user-controlled.
 */
@Serializable
enum class ZIndexMode {
    AUTO,
    BASIC,
    MANUAL
}
