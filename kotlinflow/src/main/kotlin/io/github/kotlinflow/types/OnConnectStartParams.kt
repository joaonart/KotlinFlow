package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Parameters passed to the onConnectStart callback when a user begins
 * dragging a new connection from a handle.
 */
@Serializable
data class OnConnectStartParams(
    val nodeId: String,
    val handleId: String,
    val handleType: HandleType
)
