package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Specifies whether a handle acts as a connection source or target.
 */
@Serializable
enum class HandleType {
    SOURCE,
    TARGET
}
