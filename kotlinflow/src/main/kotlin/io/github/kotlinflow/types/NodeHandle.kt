package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Describes a handle's position and dimensions on a node.
 */
@Serializable
data class NodeHandle(
    val id: String? = null,
    val type: HandleType = HandleType.SOURCE,
    val position: Position = Position.RIGHT,
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 12f,
    val height: Float = 12f
)
