package io.github.kotlinflow.types

import androidx.compose.ui.Alignment
import kotlinx.serialization.Serializable

/**
 * Supported positions for overlay panels on the canvas.
 */
@Serializable
enum class PanelPosition {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    val alignment: Alignment
        get() = when (this) {
            TOP_LEFT -> Alignment.TopStart
            TOP_CENTER -> Alignment.TopCenter
            TOP_RIGHT -> Alignment.TopEnd
            CENTER_LEFT -> Alignment.CenterStart
            CENTER -> Alignment.Center
            CENTER_RIGHT -> Alignment.CenterEnd
            BOTTOM_LEFT -> Alignment.BottomStart
            BOTTOM_CENTER -> Alignment.BottomCenter
            BOTTOM_RIGHT -> Alignment.BottomEnd
        }
}
