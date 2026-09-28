package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Controls how connections are validated between handles.
 * - [STRICT]: Connections are only allowed between source and target handles.
 * - [LOOSE]: Connections can be made between any two handles.
 */
@Serializable
enum class ConnectionMode {
    STRICT,
    LOOSE
}
