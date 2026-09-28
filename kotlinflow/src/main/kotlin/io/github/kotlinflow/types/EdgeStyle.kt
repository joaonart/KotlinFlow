package io.github.kotlinflow.types

import androidx.compose.ui.graphics.Color
import io.github.kotlinflow.utils.ColorSerializer
import kotlinx.serialization.Serializable

/**
 * Visual style overrides for an individual edge.
 * Set properties to null to use theme defaults.
 */
@Serializable
data class EdgeStyle(
    val strokeColor: @Serializable(with = ColorSerializer::class) Color? = null,
    val strokeWidth: Float? = null,
    val opacity: Float? = null
)
