package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Controls the color scheme used by KotlinFlow components.
 */
@Serializable
enum class ColorMode {
    LIGHT,
    DARK,
    SYSTEM
}
