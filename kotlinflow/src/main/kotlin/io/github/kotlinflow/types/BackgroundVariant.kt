package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * The pattern drawn on the canvas background.
 */
@Serializable
enum class BackgroundVariant {
    /** Regular dot grid pattern. */
    DOTS,
    /** Horizontal and vertical line grid. */
    LINES,
    /** Cross-hair pattern at each grid intersection. */
    CROSS,
    /** No background pattern drawn. */
    NONE
}
