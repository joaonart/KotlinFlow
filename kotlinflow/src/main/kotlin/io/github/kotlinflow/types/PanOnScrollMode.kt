package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Controls the directional constraint for pan gestures.
 */
@Serializable
enum class PanOnScrollMode {
    FREE,
    VERTICAL,
    HORIZONTAL
}
