package io.github.kotlinflow.types

import io.github.kotlinflow.models.EdgeMarker
import io.github.kotlinflow.models.EdgeType
import kotlinx.serialization.Serializable

/**
 * Default properties applied to newly created edges.
 */
@Serializable
data class DefaultEdgeOptions(
    val type: EdgeType = EdgeType.DEFAULT,
    val animated: Boolean = false,
    val markerStart: EdgeMarker? = null,
    val markerEnd: EdgeMarker? = null
)
