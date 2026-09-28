package io.github.kotlinflow.types

import kotlinx.serialization.Serializable

/**
 * Options for the fitView canvas operation.
 */
@Serializable
data class FitViewOptions(
    val padding: Float = 80f,
    val includeHiddenNodes: Boolean = false,
    val minZoom: Float = 0.1f,
    val maxZoom: Float = 1.5f,
    val durationMillis: Long? = 300L,
    val nodeIds: List<String>? = null
)
