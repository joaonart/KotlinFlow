package io.github.kotlinflow.models

import kotlinx.serialization.Serializable

/**
 * Represents a pending connection request between two handles.
 * Created during interactive edge drawing and passed to onConnect and isValidConnection callbacks.
 */
@Serializable
data class Connection(
    val source: String,
    val target: String,
    val sourceHandle: String? = null,
    val targetHandle: String? = null
)

/**
 * Type alias for connection validation functions.
 */
typealias IsValidConnection = (Connection) -> Boolean
