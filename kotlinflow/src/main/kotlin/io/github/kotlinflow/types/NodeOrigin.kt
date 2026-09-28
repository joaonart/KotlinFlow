package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Defines the anchor point for node positioning.
 *
 * (0, 0) means top-left (default), (0.5, 0.5) means center.
 */
@Serializable
data class NodeOrigin(
    val x: Float = 0f,
    val y: Float = 0f
) {
    companion object {
        val TopLeft = NodeOrigin(0f, 0f)
        val Center = NodeOrigin(0.5f, 0.5f)
    }
}
