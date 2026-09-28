package io.github.kotlinflow.types

import androidx.compose.ui.graphics.Color
import io.github.kotlinflow.utils.ColorSerializer
import kotlinx.serialization.Serializable

/**
 * Visual style overrides for an individual node.
 * Set properties to null to use theme defaults.
 */
@Serializable
data class NodeStyle(
    val backgroundColor: @Serializable(with = ColorSerializer::class) Color? = null,
    val borderColor: @Serializable(with = ColorSerializer::class) Color? = null,
    val borderWidth: Float? = null,
    val borderRadius: Float? = null,
    val opacity: Float? = null
)
