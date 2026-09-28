package io.github.kotlinflow.models

import kotlinx.serialization.Serializable

/**
 * Camera state for the canvas, describing pan offset and zoom level.
 */
@Serializable
data class Viewport(
    val x: Float = 0f,
    val y: Float = 0f,
    val zoom: Float = 1f
) {
    val clampedZoom: Float
        get() = zoom.coerceIn(MinZoom, MaxZoom)

    companion object {
        val Identity = Viewport(x = 0f, y = 0f, zoom = 1f)
        const val MinZoom: Float = 0.1f
        const val MaxZoom: Float = 4.0f
    }
}
