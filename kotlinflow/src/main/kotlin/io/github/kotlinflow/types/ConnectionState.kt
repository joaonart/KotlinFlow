package io.github.kotlinflow.types

import io.github.kotlinflow.models.XYPosition
import kotlinx.serialization.Serializable

/**
 * The current state of an in-progress connection drag.
 *
 * Mirrors ReactFlow v12's `ConnectionState` from the `useConnection` hook.
 * When no connection is in progress, the value is null.
 */
@Serializable
data class ConnectionState(
    /**
     * Whether the connection target is valid: null = unknown, true = valid, false = invalid.
     */
    val isValid: Boolean? = null,
    /** Start position in canvas coordinates. */
    val from: XYPosition,
    /** The source handle. */
    val fromHandle: NodeHandle,
    /** The source handle direction. */
    val fromPosition: Position,
    /** Type-erased source node snapshot. */
    val fromNode: AnyNodeSnapshot,
    /** Current drag position in canvas coordinates. */
    val to: XYPosition,
    /** Target handle if snapped to one. */
    val toHandle: NodeHandle? = null,
    /** Target handle direction. */
    val toPosition: Position = Position.RIGHT,
    /** Target node snapshot if snapped to one. */
    val toNode: AnyNodeSnapshot? = null
)
